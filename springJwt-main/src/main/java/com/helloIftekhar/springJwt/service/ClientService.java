package com.helloIftekhar.springJwt.service;

import com.helloIftekhar.springJwt.model.Client;
import com.helloIftekhar.springJwt.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClientService {
    private final ClientRepository clientRepository;

    @Autowired
    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public Client getClientById(Long clientId) {
        return clientRepository.findById(clientId);
    }
}
