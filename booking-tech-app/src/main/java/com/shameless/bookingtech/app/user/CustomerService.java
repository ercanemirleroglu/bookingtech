package com.shameless.bookingtech.app.user;

import com.shameless.bookingtech.domain.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    public CustomerDto getByUsername(String username) {
        return customerRepository.findByUsername(username).map(customerMapper::toDto)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
    }
}
