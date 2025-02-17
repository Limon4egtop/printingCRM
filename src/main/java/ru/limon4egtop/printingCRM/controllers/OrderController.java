package ru.limon4egtop.printingCRM.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;
import ru.limon4egtop.printingCRM.Services.impl.ClientsServiceImp;
import ru.limon4egtop.printingCRM.Services.impl.EmployeeServiceImp;
import ru.limon4egtop.printingCRM.Services.impl.FilesPathServiceImp;
import ru.limon4egtop.printingCRM.Services.impl.OrderServiceImp;
import ru.limon4egtop.printingCRM.models.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Controller
@RequestMapping("/order")
public class OrderController extends MainController {
    private final FilesPathServiceImp filesPathServiceImp;

    @Autowired
    public OrderController(final ClientsServiceImp clientsServiceImp, final FilesPathServiceImp filesPathServiceImp, final OrderServiceImp orderServiceImp, final EmployeeServiceImp employeeServiceImp) {
        super(clientsServiceImp, orderServiceImp, employeeServiceImp);
        this.filesPathServiceImp = filesPathServiceImp;
    }

    @PostMapping("/add")
    public String getAddOrderPage(Model model) {
        model.addAttribute("clientsList", this.clientsServiceImp.getAllClients());
        model.addAttribute("machineTypes", Machine.values());
        return "addOrder";
    }

    @GetMapping("/edit/{orderId}")
    private String getEditOrderPage(@PathVariable("orderId") final Long orderId,
                                    final Principal principal,
                                    Model model) {
        String currentUsername = principal.getName();
        if (!hasAccessToOrder(orderId, currentUsername) || isPrinter()) {
            return "error/error-403";
        }

        Orders order = this.orderServiceImp.getOrderById(orderId);
        model.addAttribute("order", order);
        model.addAttribute("clientName", this.clientsServiceImp.getClientByID(order.getClientId()).getCompanyName());
        model.addAttribute("machineTypes", Machine.values());
        return "editOrder";
    }

    @PostMapping("/search")
    public String searchOrders(
            @RequestParam(name = "orderNumber", required = false) final Long orderNumber,
            @RequestParam(name = "companyName", required = false) final String companyName,
            @RequestParam(name = "managerName", required = false) final String managerName,
            @RequestParam(name = "paymentStatus", required = false) final String paymentStatus,
            @RequestParam(name = "orderStatus", required = false) final String orderStatus,
            @RequestParam(name = "comment", required = false) final String comment,
            @RequestParam(name = "dateEnd", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final LocalDate dateEnd,
            Model model) {
        String currentUsername = isOwner() ? null : getAuthenticationUserId();
        if (orderNumber == null && (companyName == null || companyName.isEmpty()) && (managerName == null || managerName.isEmpty()) && (paymentStatus == null || paymentStatus.isEmpty()) && orderStatus.isEmpty() && comment.isEmpty() && dateEnd == null) {
            return "redirect:/";
        }
        List<Orders> orders = null;
        System.out.println("isPrinter() " + isPrinter());
        System.out.println("isOwner() " + isOwner());
        System.out.println("isManager() " + isManager());
        if (isOwner() || isManager()) {
             orders = this.orderServiceImp.getOrdersByFilters(orderNumber, companyName, managerName, paymentStatus, orderStatus, comment, dateEnd, currentUsername);
            model.addAttribute("clientsMap", getCompanysMap());
            model.addAttribute("employeeMap", getEmployeeMap());
        }
        else if (isPrinter()) {
            orders = this.orderServiceImp.getOrdersByFiltersForPrinter(orderNumber, comment, dateEnd);
        }
        model.addAttribute("orders", orders);
        return "mainPage";
    }

    @PostMapping("/addNew/{orderId}")
    public RedirectView addNew(@PathVariable(name = "orderId") final Long editOrderId,
                               @RequestParam(name = "score", required = false) final MultipartFile score,
                               @RequestParam(name = "paymentStatus", required = false) final String paymentStatus,
                               @RequestParam(name = "comment", required = false) final String comment,
                               @RequestParam(name = "meketList", required = false) final MultipartFile[] meketList,
                               @RequestParam(name = "blankOrder", required = false) final MultipartFile blankOrder,
                               @RequestParam(name = "machine", required = false) final String machine,
                               @RequestParam(name = "finishOrderDate", required = false) final String finishOrderDate,
                               @RequestParam(name = "clientName", required = false) final String clientName,
                               @RequestParam(name = "newClientName", required = false) final String newClientName,
                               @RequestParam(name = "newClientPhone", required = false) final String newClientPhone,
                               @RequestParam(name = "newClientContact", required = false) final String newClientContact,
                               @RequestParam(name = "newClientEmail", required = false) final String newClientEmail,
                               @RequestParam(name = "newClientWebsite", required = false) final String newClientWebsite) throws IOException {
        Orders newOrder = new Orders();
        if (editOrderId != null && editOrderId != -1) {
            newOrder = this.orderServiceImp.getOrderById(editOrderId);
        }
        if (finishOrderDate != null) {
            newOrder.setDateEnd(LocalDate.parse(finishOrderDate));
        }
        if (paymentStatus != null) {
            newOrder.setPaymentStatus(paymentStatus);
        }
        if (comment != null) {
            newOrder.setComment(comment);
        }
        if (machine != null) {
            try {
                int machineCode = Integer.parseInt(machine);
                Machine machineType = Machine.fromCode(machineCode);
                newOrder.setMachine(machineType);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid machine value: " + machine, e);
            }
        }
        Long clientId = null;
        if (clientName != null && !clientName.isEmpty()) {
            clientId = this.clientsServiceImp.getClientByCompanyName(clientName).getId();
        }
        else if (newClientName != null && !newClientName.isEmpty()
                && newClientPhone != null && !newClientPhone.isEmpty()
                && newClientContact != null && !newClientContact.isEmpty()
                && newClientEmail != null && !newClientEmail.isEmpty()) {
            Clients newClient = new Clients(newClientName, newClientPhone, newClientContact, newClientEmail);
            if (newClientWebsite != null && !newClientWebsite.isEmpty()) {
                newClient.setWebsite(newClientWebsite);
            }
            clientId = this.clientsServiceImp.addClient(newClient).getId();
        }
        if (clientId == null) {
            return new RedirectView("/error/errorClientNameNull");
        }
        newOrder.setClientId(clientId);
        newOrder.setManagerUsername(getAuthenticationUserId());
        Long orderId = this.orderServiceImp.addOrder(newOrder).getId();
        String staticFolderPath = "/Users/vladimirfilimonov/IdeaProjects/printingCRM/src/main/resources/static";
        saveScore(score, orderId, staticFolderPath);
        saveMaketList(meketList, orderId, staticFolderPath);
        saveBlankOrder(blankOrder, orderId, staticFolderPath);
        return new RedirectView("/");
    }

    private void saveScore(final MultipartFile score,
                           final Long orderId,
                           final String staticFolderPath) throws IOException {
        List<FilesPath> filesData = this.filesPathServiceImp.getByOrderIdAndFileType(orderId, "scope");
        if (!filesData.isEmpty() && score.getBytes().length > 0) {
            deleteFile(filesData, staticFolderPath);
        }
        if (score.getBytes().length > 0) {
            String fileExtension = score.getOriginalFilename().substring(score.getOriginalFilename().lastIndexOf("."));
            String fileName = "Scope_" + orderId + fileExtension;
            Path scorePath = Paths.get(staticFolderPath+"/documents/score", fileName);
            Files.write(scorePath, score.getBytes());
            FilesPath newFile = new FilesPath(orderId, "scope", "/documents/score/"+fileName);
            this.filesPathServiceImp.addFile(newFile);
        }
    }

    private void saveMaketList(final MultipartFile[] meketList,
                               final Long orderId,
                               final String staticFolderPath) throws IOException {
        List<FilesPath> filesData = this.filesPathServiceImp.getByOrderIdAndFileType(orderId, "layout");
        if (!filesData.isEmpty() && meketList.length >= 1) {
            deleteFile(filesData, staticFolderPath);
        }
        if (meketList[0].getBytes().length >= 1) {
            for (int i = 0; i < meketList.length; i++) {
                String fileExtension = meketList[i].getOriginalFilename().substring(meketList[i].getOriginalFilename().lastIndexOf("."));
                String fileName = "Layout_" + orderId + "_" + i+1 + fileExtension;
                Path meketListPath = Paths.get(staticFolderPath + "/documents/maket", fileName);
                Files.write(meketListPath, meketList[i].getBytes());
                FilesPath newFile = new FilesPath(orderId, "layout", "/documents/maket/" + fileName);
                this.filesPathServiceImp.addFile(newFile);
            }
        }
    }

    private void saveBlankOrder(final MultipartFile blankOrder,
                                  final Long orderId,
                                  final String staticFolderPath) throws IOException {
        List<FilesPath> filesData = this.filesPathServiceImp.getByOrderIdAndFileType(orderId, "blank");
        if (!filesData.isEmpty() && blankOrder.getBytes().length > 0) {
            deleteFile(filesData, staticFolderPath);
        }
        if (blankOrder.getBytes().length > 0) {
            String fileExtension = blankOrder.getOriginalFilename().substring(blankOrder.getOriginalFilename().lastIndexOf("."));
            String fileName = "Blank_" + orderId + fileExtension;
            Path blankOrderPath = Paths.get(staticFolderPath+"/documents/blankOrder", fileName);
            Files.write(blankOrderPath, blankOrder.getBytes());
            FilesPath newFile = new FilesPath(orderId, "blank", "/documents/blankOrder/"+fileName);
            this.filesPathServiceImp.addFile(newFile);
        }
    }

    private void deleteFile(final List<FilesPath> filesData, final String staticFolderPath) throws IOException {
        for (FilesPath file : filesData) {
            Path path = Paths.get(staticFolderPath + file.getFileUrl());
            try {
                Files.delete(path);
                this.filesPathServiceImp.deleteFile(file);
            } catch (IOException e) {
                throw new IOException("Ошибка при удалении файла: " + path, e);
            }
        }
    }

    @GetMapping("/info/{orderId}")
    public String getOrderInfoPage(Model model,
                                   @PathVariable("orderId") final Long orderId,
                                   final Principal principal) {
        String currentUsername = principal.getName();
        if (!hasAccessToOrder(orderId, currentUsername)) {
            return "error/error-403";
        }
        Orders order = this.orderServiceImp.getOrderById(orderId);
        model.addAttribute("orderInfo", order);
        List<FilesPath> filesPathList = this.filesPathServiceImp.getByOrderIdAndFileType(orderId, "layout");
        model.addAttribute("filesPathList", filesPathList);
        model.addAttribute("clientCompany", this.clientsServiceImp.getClientByID(order.getClientId()));
        return "orderInfo";
    }


    @GetMapping("/info/downloadFile/{orderId}/{fileType}")
    public ResponseEntity<Resource> getDownloadFile(@PathVariable("orderId") final Long orderId,
                                                    @PathVariable("fileType") final String fileType,
                                                    final Principal principal) throws IOException {
        String currentUsername = principal.getName();
        if (!hasAccessToOrder(orderId, currentUsername)) {
            return ResponseEntity.status(403).build();
        }

        List<FilesPath> filesPathList = this.filesPathServiceImp.getByOrderIdAndFileType(orderId, fileType);

        if (Objects.equals(fileType, "blank") || Objects.equals(fileType, "scope")) {
            String fileUrl = "/Users/vladimirfilimonov/IdeaProjects/printingCRM/src/main/resources/static" + filesPathList.get(0).getFileUrl();     // TODO: исправить, чтобы брался не из системы, а из проекта
            Path filePath = Paths.get(fileUrl);
            Resource fileResource = new FileSystemResource(filePath);
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filePath.getFileName().toString() + "\"");
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(filePath.toFile().length())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(fileResource);
        } else if (Objects.equals(fileType, "layout")) {
            // Архивирование файлов типа "layout"
            String zipFileName = "order_" + orderId + "_layouts.zip";
            Path zipFilePath = Paths.get(System.getProperty("java.io.tmpdir"), zipFileName);

            try (ZipOutputStream zipOutputStream = new ZipOutputStream(Files.newOutputStream(zipFilePath))) {
                for (FilesPath file : filesPathList) {
                    Path filePath = Paths.get("/Users/vladimirfilimonov/IdeaProjects/printingCRM/src/main/resources/static" + file.getFileUrl()); // TODO: исправить на проектные пути
                    try (InputStream inputStream = Files.newInputStream(filePath)) {
                        ZipEntry zipEntry = new ZipEntry(filePath.getFileName().toString());
                        zipOutputStream.putNextEntry(zipEntry);
                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = inputStream.read(buffer)) > 0) {
                            zipOutputStream.write(buffer, 0, len);
                        }
                        zipOutputStream.closeEntry();
                    }
                }
            }

            Resource zipResource = new FileSystemResource(zipFilePath);
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + zipFileName + "\"");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(zipFilePath.toFile().length())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(zipResource);
        }

        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/edit/sendOnPrint/{orderId}")
    private String sendOnPrint(@PathVariable("orderId") final Long orderId) {

        Orders order = this.orderServiceImp.getOrderById(orderId);
        order.setOrderStatus("На печати");
        this.orderServiceImp.addOrder(order);
        return "redirect:/order/info/" + orderId;
    }

    @GetMapping("/edit/readyForDelivery/{orderId}")
    private String readyForDelivery(@PathVariable("orderId") final Long orderId) {
        Orders order = this.orderServiceImp.getOrderById(orderId);
        order.setOrderStatus("Готов к выдаче");
        this.orderServiceImp.addOrder(order);
        return "redirect:/";
    }

    @GetMapping("/edit/issued/{orderId}")
    private String issued(@PathVariable("orderId") final Long orderId) {
        Orders order = this.orderServiceImp.getOrderById(orderId);
        order.setOrderStatus("Выдан");
        this.orderServiceImp.addOrder(order);
        return "redirect:/order/info/" + orderId;
    }

    private boolean hasAccessToOrder(final Long orderId, final String username) {
        Orders order = this.orderServiceImp.getOrderById(orderId);
        if (order == null) {
            throw new IllegalArgumentException("Order not found: " + orderId);
        }
        return order.getManagerUsername().equals(username) ||
                isUserOwner(username) ||
                isPrinterWithAccess(username, order.getOrderStatus());
    }
}

