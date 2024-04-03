package com.helloIftekhar.springJwt.service;

import com.helloIftekhar.springJwt.model.*;
import com.helloIftekhar.springJwt.repository.*;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.border.DashedBorder;
import com.itextpdf.layout.border.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class Impl implements Interface {

    @Autowired
    private NotificationService notificationService;
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private BusinessInfoRepository businessRepo;
    @Autowired
    private InvoiceRepository invoiceRepo;
    @Autowired
    private ItemsRepository itemRepo;
    @Autowired
    private QuoteRepository quoteRepo;
    @Autowired
    private ClientRepository clientRepo;
    @Autowired
    private ClientAddressRepository clientAddressRepo;
    @Autowired
    private Payfast payfast;
    @Autowired
    private PocketRepository  pocketRepo;
    String link="";
    //email settings
    @Autowired
    private JavaMailSender jmSender;

    @Override
    public boolean deleteInvoice(int id, String email) {
        return false;
    }

    @Override
    public Invoice searchInvoice(int id, String email) {
        return null;
    }

    @Override
    public List<Invoice> getTop5UnpaidInvoicesByEmail(String email, String paymentStatus) {
        return invoiceRepo.findTop5ByUserEmailAndPaymentStatusOrderByInvoiceIdDesc(email, paymentStatus);
    }
    public BusinessInfo getAllBusinessInfo(String email) {
        Optional<User> userOptional = userRepo.findByEmail(email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return businessRepo.findByUserEmail(email);
        } else {
            return null;
        }
    }

    public List<Invoice> homeTop1Invoice(String email) {
        Optional<User> user = userRepo.findByEmail(email);
        return user.map(value -> invoiceRepo.findTop1ByUserOrderByInvoiceIdDesc(value)).orElse(Collections.emptyList());
    }

    @Override
    public List<Quote> homeTop5Quote(String email) {
        Optional<User> user = userRepo.findByEmail(email);
        return user.map(value -> quoteRepo.findTop5ByUserOrderByIdDesc(value)).orElse(Collections.emptyList());
    }


    @Override
    @Transactional
    public boolean createInvoiceOrQuote(String email, ClientAddressInvoiceQuoteItems caiqi) throws IOException {
        Optional<User> userOptional = userRepo.findByEmail(email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            Client client = caiqi.getClient();
            client.setUser(user);
            clientRepo.save(client); // Save the client

            ClientAddress clientAddress = caiqi.getClientAddress();
            clientAddress.setClient(client); // Set the client for the client address
            clientAddressRepo.save(clientAddress); // Save the client address

            // If you want to associate the client's ID with the quote, set it here
            Quote quote = caiqi.getQuote();
            quote.setUser(user);
            quote.setClient(client); // Set the client for the quote
            quote.setClientAddress(clientAddress); // Set the client address for the quote

            if (caiqi.getType().equals("Invoice")) {
                // Handle invoice creation
                Invoice invoice = caiqi.getInvoice();
                invoice.setUser(user);
                invoice.setClient(client); // Set the client for the invoice
                invoice.setClientAddress(clientAddress); // Set the client address for the invoice
                invoice.setDate(LocalDateTime.now());
                invoice.setPaymentStatus("unpaid");
                invoice.setCompanyName(caiqi.getCompanyName());
                List<Items> items = caiqi.getItems();
                double total = 0;
                for (Items item : items) {
                    item.setInvoice(invoice);
                    total += item.getPrice() * item.getQty();
                }
                invoice.setTotalAmount(total);

                Random random = new Random();
                int randomNumber;
                do {
                    randomNumber = random.nextInt(9000) + 1000;
                } while (invoiceRepo.existsByInvoiceNo(randomNumber));

                invoice.setInvoiceNo(randomNumber);
                invoiceRepo.save(invoice);

                for (Items item : items) {
                    item.setInvoice(invoice);
                    itemRepo.save(item);
                }

                generateEmailPdf(caiqi.getType(), invoice.getDate(), user, invoice.getTotalAmount(),
                        items, client, clientAddress, randomNumber);

                // Create notification message for invoice
                String notificationMessage = generateNotificationMessageForInvoice("Invoice", invoice.getInvoiceNo());

                // Send notification
                sendNotification(email, notificationMessage, invoice, null);
                return true;

            } else if (caiqi.getType().equals("Quote")) {
                quote.setDate(LocalDateTime.now());
                quote.setCompanyName(caiqi.getCompanyName());
                List<Items> items = caiqi.getItems();
                double total = 0;
                for (Items item : items) {
                    item.setQuote(quote);
                    total += item.getPrice() * item.getQty();
                }
                quote.setTotalAmount(total);

                Random random = new Random();
                int randomNumber;
                do {
                    randomNumber = random.nextInt(9000) + 1000;
                } while (quoteRepo.existsByQuoteNo(randomNumber));

                quote.setQuoteNo(randomNumber);
                quoteRepo.save(quote);

                for (Items item : items) {
                    item.setQuote(quote);
                    itemRepo.save(item);
                }

                // Handle email and notification for quote
                generateEmailPdf(caiqi.getType(), LocalDateTime.from(quote.getDate()), user, quote.getTotalAmount(),
                        items, client, clientAddress, randomNumber);

                // Create notification message for quote
                String notificationMessage = generateNotificationMessageForQuote("Quote", quote.getQuoteNo());

                // Send notification
                sendNotification(email, notificationMessage, null, quote);
            }
            return true;
        }
        return false;
    }




    public String generateNotificationMessageForInvoice(String type, int invoiceNo) {
        return "Invoice number #" + invoiceNo + " has been successfully added.";
    }

    public String generateNotificationMessageForQuote(String type, int quoteNo) {
        return "Quote number #" + quoteNo + " has been successfully added.";
    }

    public void sendNotification(String email, String message, Invoice invoice, Quote quote) {
        try {
            MimeMessage mimeMessage = jmSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            helper.setTo(email);
            helper.setFrom("inv@mfactorydev.co.za"); // Change this to your sender email
            helper.setSubject("Notification"); // You can set the subject as needed
            helper.setText(message, false);

            jmSender.send(mimeMessage);

            // Save notification to the database
            Notification notification = new Notification();
            notification.setMessage(message);
            notification.setRecipient(email);
            notification.setSentAt(LocalDateTime.now());

            // Set the invoice or quote based on what is provided
            if (invoice != null) {
                notification.setInvoice(invoice);
            } else if (quote != null) {
                notification.setQuote(quote);
            }

            notificationRepository.save(notification);
        } catch (MessagingException e) {
            e.printStackTrace(); // Handle the exception appropriately, like logging it
        }
    }

    public boolean updateQuote(String email, ClientAddressInvoiceQuoteItems caiqi, Long quoteNo) throws IOException {
        Optional<User> userOptional = userRepo.findByEmail(email);
        double total = 0;

        if (userOptional.isPresent()) {
            Optional<Quote> quoteOptional = quoteRepo.findByQuoteNo(quoteNo);
            if (quoteOptional.isPresent()) {
                Quote quote = quoteOptional.get();
                quote.setDate(LocalDateTime.now()); // Update date to current date

                // Fetch the existing Client associated with the Quote
                Client existingClient = quote.getClient();

                // Update the existing Client's properties
                Client newClient = caiqi.getClient();
                existingClient.setF_name(newClient.getF_name());
                existingClient.setL_name(newClient.getL_name());
                existingClient.setEmail(newClient.getEmail());
                existingClient.setPhoneNo(newClient.getPhoneNo());
                clientRepo.save(existingClient);

                // Fetch the existing ClientAddress associated with the Quote
                ClientAddress existingClientAddress = quote.getClientAddress();

                // Update the existing ClientAddress's properties
                ClientAddress newClientAddress = caiqi.getClientAddress();
                existingClientAddress.setStreetNo(newClientAddress.getStreetNo());
                existingClientAddress.setStreetName(newClientAddress.getStreetName());
                existingClientAddress.setTown(newClientAddress.getTown());
                existingClientAddress.setCity(newClientAddress.getCity());
                existingClientAddress.setPostalCode(newClientAddress.getPostalCode());
                clientAddressRepo.save(existingClientAddress);

                // Update items
                List<Items> newItems = caiqi.getItems();
                for (Items newItem : newItems) {
                    // Set the quote reference for the new item
                    newItem.setQuote(quote);
                    // Update total amount
                    total += newItem.getPrice() * newItem.getQty();
                }

                // Delete existing items associated with the quote number
                itemRepo.deleteByQuoteNo(quoteNo);

                // Clear existing items in the quote
                quote.getItems().clear();

                // Add the new items to the quote
                quote.getItems().addAll(newItems);

                // Update total amount in the quote
                quote.setTotalAmount(total);

                // Save updated quote
                quoteRepo.save(quote);

                // Generate and send email PDF
                generateEmailPdf("Quote", LocalDateTime.now(), userOptional.get(), quote.getTotalAmount(),
                        newItems, existingClient, existingClientAddress, quote.getQuoteNo());

                // Create and send notification message for quote
                String notificationMessage = "Quote number #" + quote.getQuoteNo() + " updated successfully";
                sendNotification(email, notificationMessage, null, quote);

                return true;
            }
        }
        return false;
    }





    @Override
    public List<Invoice> getAllInvoices(String email) {
        try {
            return invoiceRepo.findByUserEmail(email);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @Override
    public List<Quote> getAllQuote(String email) {
        try {
            return quoteRepo.findByUserEmail(email);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public void sendDoc(String to, String from,String path, Client client, String type, String link) {
        try {
            MimeMessage mimeMessage = jmSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            // Set basic email properties
            helper.setTo(to);
            helper.setFrom("inv@mfactorydev.co.za");
            helper.setSubject(type + " attachment");

            if(type.equals("Invoice")) {
                helper.setText("Dear " + client.getF_name() + ",\n\nAttached is your " + type + ".\n" +
                        "A link is provided to complete the payment. " + link + " \nThank you for your time.\n\nKind Regards\n", false);
            }
            else
            {
                helper.setText("Dear " + client.getF_name() + ",\n\nAttached is your " + type + ".\n" +
                        "Thank you for your time.\n\nKind Regards\n", false);

            }

            // Attach the file from the specified path
            FileSystemResource file = new FileSystemResource(new File(path));
            helper.addAttachment(Objects.requireNonNull(file.getFilename()), file);

            // Send the email
            jmSender.send(mimeMessage);
        } catch (MessagingException e) {
            // Handle exception (e.g., log error)
            e.printStackTrace();
        }
    }
    @Override
    public double invoiceTotalAmt(String email) {
        return invoiceRepo.getTotalUnpaidAmount(email);
    }
    @Override
    public void changeStatus(String email, int invoiceNo,double amount) {
        Optional<User> userOptional = userRepo.findByEmail(email);
        System.out.println (userOptional);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            Invoice invoice = invoiceRepo.findByInvoiceNoAndUser(invoiceNo, user);
            if (invoice != null) {
                System.out.println("Testing");
                System.out.println(user.getEmail());
                System.out.println(invoice.getDate());
                System.out.println("Testing");

                invoice.setPaymentStatus("Paid");
                Pocket pocket=pocketRepo.findByUser(user);
                double  oldAmt=pocket.getBalance();
                pocket.setBalance(oldAmt+amount);
                pocketRepo.save(pocket);
                invoiceRepo.save(invoice);
            } else {
                System.out.println("Invoice not found for invoice number " + invoiceNo);
            }
        } else {
            System.out.println("User not found with email " + email);
        }
    }
    @Override
    public double getBalance(String email) {

        return pocketRepo.findBalanceByUserEmail(email);
    }


    public void generateEmailPdf(String type, LocalDateTime localDateTime, User user,
                                 double totalAmount, List<Items> items,
                                 Client client, ClientAddress clientA,
                                 int randomNo) throws IOException {
        String path = "invoice.pdf";
        PdfWriter pdfWriter = new PdfWriter(path);
        PdfDocument pdfDocument = new PdfDocument(pdfWriter);
        pdfDocument.setDefaultPageSize(PageSize.A4);
        Document document = new Document(pdfDocument);
        //page spec end
       BusinessInfo businessInfo=businessRepo.findByUser(user);

        //change LocalDate to String
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy HH:mm");
        String dateString = localDateTime.format(formatter);

        //header
        float threecol=190f;
        float twocol=285f;
        float twocol150=twocol+150f;
        float[] twocolumnWidth ={twocol150,twocol};
        float [] threeColumnWidth={threecol,threecol,threecol};
        float fullwidth []={threecol * 3};
        Paragraph onesp = new Paragraph("\n");

        Table table = new Table(twocolumnWidth);
        table.addCell(new Cell().add(type).setFontSize(20f).setBorder(Border.NO_BORDER).setBold());
        Table nestedTable = new Table(new float[]{twocol/2, twocol/2});
        nestedTable.addCell(getHeaderTextCell(type+" No:"));
        nestedTable.addCell(getHeaderTextCellValue(String.valueOf(randomNo)));
        nestedTable.addCell(getHeaderTextCell("Issue date:"));
        nestedTable.addCell(getHeaderTextCellValue(dateString));


        table.addCell(new Cell().add(nestedTable).setBorder(Border.NO_BORDER));

        Border gb=new SolidBorder(Color.GRAY, 1f/2f);
        Table divider=new Table (fullwidth);
        divider.setBorder(gb);
        document.add(table);
        document.add(onesp);
        document.add(divider);
        document.add(onesp);

        Table twoColTable = new Table(twocolumnWidth);
        twoColTable.addCell(getBilling("Company Information"));
        twoColTable.addCell(getBilling("Client Information"));
        document.add(twoColTable.setMarginBottom(12f));

        Table twoColTable2 = new Table(twocolumnWidth);
        twoColTable2.addCell(getCell10left("Company Name", true));
        twoColTable2.addCell(getCell10left("Name", true));
        twoColTable2.addCell(getCell10left(businessInfo.getCompanyName(), false));
        twoColTable2.addCell(getCell10left(client.getF_name()+" "+client.getL_name(), false));



        document.add(twoColTable2);

        Table twoColTable3 = new Table(twocolumnWidth);
        twoColTable3.addCell(getCell10left("Company Number", true));
        twoColTable3.addCell(getCell10left("Address", true));
        twoColTable3.addCell(getCell10left(businessInfo.getTaxNo(), false));
        twoColTable3.addCell(getCell10left(String.valueOf(clientA.getStreetNo()+", "+clientA.getStreetName()+","+clientA.getTown()
                +"\n"+clientA.getCity()+"\n"+String.valueOf(clientA.getPostalCode())), false));
        document.add(twoColTable3);

        float oneColumnwidth[]={twocol150};

        Table oneColTable1=new Table(oneColumnwidth);
        oneColTable1.addCell(getCell10left("Company Address", true));
        oneColTable1.addCell(getCell10left(businessInfo.getStreetNo()+" "+businessInfo.getStreetName()+", "+"\n" +
                businessInfo.getTown()+", "+businessInfo.getCity()+"\n", false));
        oneColTable1.addCell(getCell10left("Email", true));
        oneColTable1.addCell(getCell10left(businessInfo.getEmail(), false));
        document.add(oneColTable1.setMarginBottom(10f));

        Table tableDivider2=new Table(fullwidth);
        Border dgb = new DashedBorder(Color.GRAY,0.5f);
        document.add(tableDivider2.setBorder(dgb));
        Paragraph ProductPara=new Paragraph("Products");

        document.add(ProductPara.setBold());
        Table threeColTable1 = new Table(threeColumnWidth);
        threeColTable1.setBackgroundColor(Color.BLACK,0.7f);

        threeColTable1.addCell(new Cell().add("Description").setBold().setFontColor(Color.WHITE).setBorder(Border.NO_BORDER));
        threeColTable1.addCell(new Cell().add("Quantity").setBold().setFontColor(Color.WHITE).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
        threeColTable1.addCell(new Cell().add("Price").setBold().setFontColor(Color.WHITE).setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER));
        document.add(threeColTable1);

        Table threeColTable2 = new Table(threeColumnWidth);

        float totalsum = (float) totalAmount;
        for(Items product:items)
        {

            threeColTable2.addCell(new Cell().add(product.getDesc()).setBorder(Border.NO_BORDER).setMarginLeft(10f));
            threeColTable2.addCell(new Cell().add(String.valueOf(product.getQty())).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
            threeColTable2.addCell(new Cell().add(String.valueOf(product.getPrice())).setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER).setMarginRight(15f));

        }
        document.add(threeColTable2.setMarginBottom(20f));
        float onetwo[]={threecol+155f,threecol*2};
        Table threecolTable4 = new Table(onetwo);
        threecolTable4.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
        threecolTable4.addCell(new Cell().add(tableDivider2).setBorder(Border.NO_BORDER));
        document.add(threecolTable4);

        Table threecolTable3 = new Table(threeColumnWidth);
        threecolTable3.addCell(new Cell().add("").setBorder(Border.NO_BORDER)).setMarginLeft(10f);
        threecolTable3.addCell(new Cell().add("Total").setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
        threecolTable3.addCell(new Cell().add(String.valueOf(totalsum)).setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER));

        document.add(threecolTable3);
        document.add(tableDivider2);
        document.add(new Paragraph("\n"));
        document.add(divider.setBorder(new SolidBorder(Color.GRAY,1)).setMarginBottom(15f));
        Table tb = new Table(fullwidth);
        tb.addCell(new Cell().add("TERMS AND CONDITIONS\n").setBold().setBorder(Border.NO_BORDER));
        List<String>TncList = new ArrayList<>();
        TncList.add("1. The Seller shall bot be liable to the buyer directly or indirectly for any loss or damage suffered by the buyer");
        TncList.add("1. The Seller warrants the products for one (1) year from the issued date");

        for (String tnc:TncList){
            tb.addCell(new Cell().add(tnc).setBorder(Border.NO_BORDER));
        }
        document.add(tb);

        document.close();

        if(type.equals("Invoice"))
        {
            link = payfast.initiatePayment(totalAmount, type, user.getEmail(),randomNo);
        }
        sendDoc(client.getEmail(), user.getEmail(), path, client,type, link);
    }

    static Cell getHeaderTextCell (String textValue){
        return new Cell().add(textValue).setBold().setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT);
    }

    static Cell getHeaderTextCellValue (String textValue){
        return new Cell().add(textValue).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT);
    }

    static Cell getBilling(String textValue){
        return new Cell().add(textValue).setFontSize(12f).setBold().setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT);
    }

    static Cell getCell10left(String textValue,Boolean isBold){
        Cell myCell=new Cell().add(textValue).setFontSize(10f).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT);
        return isBold ?myCell.setBold():myCell;
    }
}



