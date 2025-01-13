package ru.limon4egtop.printingCRM.Services;

import ru.limon4egtop.printingCRM.models.Orders;

import java.time.LocalDate;
import java.util.List;

public interface OrderService {
    Orders addOrder(final Orders order);
    Orders getOrderById(final Long id);
    List<Orders> getAll();
    List<Orders> getOrdersByClientId(final Long clientId);
    List<Orders> getOrdersByManagerUsername(final String username);
    List<Orders> getOrdersByClientIdAndManagerUsername(final Long clientId, final String currentUsername);
    List<Orders> getOrdersByOrderStatus(final String orderStatus);
    List<Orders> getOrdersByFilters(final Long orderNumber,
                                    final String companyName,
                                    final String managerName,
                                    final String paymentStatus,
                                    final String orderStatus,
                                    final String comment,
                                    final LocalDate dateEnd,
                                    final String currentUsername);

    List<Orders> getOrdersByFiltersForPrinter(final Long orderNumber,
                                    final String comment,
                                    final LocalDate dateEnd);
}
