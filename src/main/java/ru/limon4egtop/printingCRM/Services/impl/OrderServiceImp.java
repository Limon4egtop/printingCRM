package ru.limon4egtop.printingCRM.Services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.limon4egtop.printingCRM.Services.OrderService;
import ru.limon4egtop.printingCRM.models.Orders;
import ru.limon4egtop.printingCRM.repos.OrderRepo;

import java.time.LocalDate;
import java.util.List;

@Service
public class OrderServiceImp implements OrderService {
    private final OrderRepo orderRepo;

    @Autowired
    public OrderServiceImp(OrderRepo orderRepo) {
        this.orderRepo = orderRepo;
    }


    @Override
    public Orders addOrder(Orders order) {
        return this.orderRepo.save(order);
    }

    @Override
    public Orders getOrderById(Long id) {
        return this.orderRepo.findOrdersById(id);
    }

    @Override
    public List<Orders> getAll() {
        return this.orderRepo.findAllByOrderByDateCreateDescIdDesc();
    }

    @Override
    public List<Orders> getOrdersByClientId(final Long clientId) {
        return this.orderRepo.findOrdersByClientIdOrderByDateCreateDescIdDesc(clientId);
    }

    @Override
    public List<Orders> getOrdersByManagerUsername(final String username) {
        return this.orderRepo.findOrdersByManagerUsernameOrderByDateCreateDescIdDesc(username);
    }

    @Override
    public List<Orders> getOrdersByClientIdAndManagerUsername(final Long clientId, final String currentUsername) {
        return this.orderRepo.findOrdersByClientIdAndManagerUsernameOrderByDateCreateDescIdDesc(clientId, currentUsername);
    }

    @Override
    public List<Orders> getOrdersByOrderStatus(final String orderStatus) {
        return this.orderRepo.findOrdersByOrderStatus(orderStatus);
    }

    @Override
    public List<Orders> getOrdersByFilters(final Long orderNumber,
                                           final String companyName,
                                           final String managerName,
                                           final String paymentStatus,
                                           final String orderStatus,
                                           final String comment,
                                           final LocalDate dateEnd,
                                           final String currentUsername) {
//        System.out.println(orderFilterDto.toString());
        return this.orderRepo.findOrdersByFilters(orderNumber, companyName, managerName, paymentStatus, orderStatus, comment, dateEnd, currentUsername);
    }
}
