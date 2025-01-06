package ru.limon4egtop.printingCRM.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.limon4egtop.printingCRM.models.Orders;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface OrderRepo extends JpaRepository<Orders, Long> {
    Orders findOrdersById(Long orderId);
    List<Orders> findAllByOrderByDateCreateDescIdDesc();
    List<Orders> findOrdersByClientIdOrderByDateCreateDescIdDesc(Long clientId);
    List<Orders> findOrdersByManagerUsernameOrderByDateCreateDescIdDesc(String managerUsername);
    List<Orders> findOrdersByClientIdAndManagerUsernameOrderByDateCreateDescIdDesc(Long clientId, String currentUsername);
    List<Orders> findOrdersByOrderStatus(String orderStatus);


    @Query("""
    SELECT o FROM Orders o
    JOIN Clients c ON o.clientId = c.id
    LEFT JOIN Employee e ON o.managerUsername = e.username
    WHERE (:id IS NULL OR o.id = :id)
      AND (:companyName IS NULL OR c.companyName LIKE CONCAT('%', :companyName, '%'))
      AND (
            :managerName IS NULL OR 
            (
                e.firstName IS NOT NULL AND e.lastName IS NOT NULL AND
                (
                    e.firstName LIKE CONCAT('%', :managerName, '%') OR
                    e.lastName LIKE CONCAT('%', :managerName, '%') OR
                    CONCAT(e.firstName, ' ', e.lastName) LIKE CONCAT('%', :managerName, '%') OR
                    CONCAT(e.lastName, ' ', e.firstName) LIKE CONCAT('%', :managerName, '%')
                )
            )
        )
      AND (:paymentStatus IS NULL OR o.paymentStatus LIKE CONCAT('%', :paymentStatus, '%'))
      AND (:orderStatus IS NULL OR o.orderStatus LIKE CONCAT('%', :orderStatus, '%'))
      AND (:comment IS NULL OR o.comment LIKE CONCAT('%', :comment, '%'))
      AND (CAST(:dateEnd AS date) IS NULL OR o.dateEnd = CAST(:dateEnd AS date))
      AND (:currentUsername IS NULL OR o.managerUsername = :currentUsername)
    ORDER BY o.dateCreate DESC
""")
    List<Orders> findOrdersByFilters(
            @Param("id") Long id,
            @Param("companyName") String companyName,
            @Param("managerName") String managerName,
            @Param("paymentStatus") String paymentStatus,
            @Param("orderStatus") String orderStatus,
            @Param("comment") String comment,
            @Param("dateEnd") LocalDate dateEnd,
            @Param("currentUsername") String currentUsername
    );

}
