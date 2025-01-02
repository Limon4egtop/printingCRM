package ru.limon4egtop.printingCRM.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;
import ru.limon4egtop.printingCRM.models.*;
import ru.limon4egtop.printingCRM.repos.ClientRepo;
import ru.limon4egtop.printingCRM.repos.EmployeeRepo;
import ru.limon4egtop.printingCRM.repos.FilesPathRepo;
import ru.limon4egtop.printingCRM.repos.OrderRepo;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Controller
@RequestMapping("/order")
public class OrderController {
    private OrderRepo orderRepo;
    private FilesPathRepo filesPathRepo;
    private ClientRepo clientRepo;
    private EmployeeRepo employeeRepo;

    @Autowired
    public OrderController(OrderRepo orderRepo, FilesPathRepo filesPathRepo, ClientRepo clientRepo, EmployeeRepo employeeRepo) {
        this.orderRepo = orderRepo;
        this.filesPathRepo = filesPathRepo;
        this.clientRepo = clientRepo;
        this.employeeRepo = employeeRepo;
    }

    private String getAuthenticationUserId() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @PostMapping("/add")
    public String getAddOrderPage(Model model) {
        model.addAttribute("clientsList", clientRepo.findAll());
        model.addAttribute("machineTypes", Machine.values());
        return "addOrder";
    }

    @GetMapping("/edit/{orderId}")
    private String getEditOrderPage(@PathVariable("orderId") Long orderId,
                                    Principal principal,
                                    Model model) {
        String currentUsername = principal.getName();
        if (!hasAccessToOrder(orderId, currentUsername)) {
            return "error/error-403";
        }
        Orders order = orderRepo.findOrdersById(orderId);
        model.addAttribute("order", order);
        model.addAttribute("clientName", clientRepo.findClientsById(order.getClientId()).getCompanyName());
        model.addAttribute("machineTypes", Machine.values());
        return "editOrder";
    }

    @PostMapping("/search")
    public String searchOrders(
            @RequestParam(name = "orderNumber", required = false) Long orderNumber,
            @RequestParam(name = "companyName", required = false) String companyName,
            @RequestParam(name = "managerName", required = false) String managerName,
            @RequestParam(name = "paymentStatus", required = false) String paymentStatus,
            @RequestParam(name = "comment", required = false) String comment,
            @RequestParam(name = "dateEnd", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateEnd,
            Model model) {
        String currentUsername = isOwner() ? null : getAuthenticationUserId();
        List<Orders> orders = orderRepo.findOrdersByFilters(
                orderNumber, companyName, managerName, paymentStatus, comment, dateEnd, currentUsername);
        model.addAttribute("orders", orders);
        model.addAttribute("clientsMap", getCompanysMap());
        model.addAttribute("employeeMap", getEmployeeMap());
        // TODO: избавиться от getCompanysMap и getEmployeeMap путем наследования mainController
        // TODO: добавить проверку на роль ищущего сотрудника. владельцу - все результаты, менеджеру - только его заказы
        return "mainPage";
    }

    private Map<String, String> getEmployeeMap() {
        return employeeRepo.findAll().stream()
                .collect(Collectors.toMap(Employee::getUsername, employee -> employee.getFirstName() + " " + employee.getLastName()));
    }


    private Map<Long, String> getCompanysMap() {
        return clientRepo.findAll().stream()
                .collect(Collectors.toMap(Clients::getId, Clients::getCompanyName));
    }

    private Boolean isOwner() {
        boolean hasRoleAdmin = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_OWNER"));
        ;
        return hasRoleAdmin;
    }

    @PostMapping("/addNew/{orderId}")
    public RedirectView addNew(@PathVariable(name = "orderId") Long editOrderId,
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
            newOrder = orderRepo.findOrdersById(editOrderId);
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
            clientId = this.clientRepo.findByCompanyName(clientName).getId();
        }
        else if (newClientName != null && !newClientName.isEmpty()
                && newClientPhone != null && !newClientPhone.isEmpty()
                && newClientContact != null && !newClientContact.isEmpty()
                && newClientEmail != null && !newClientEmail.isEmpty()) {
            Clients newClient = new Clients(newClientName, newClientPhone, newClientContact, newClientEmail);
            if (newClientWebsite != null && !newClientWebsite.isEmpty()) {
                newClient.setWebsite(newClientWebsite);
            }
            clientId = clientRepo.save(newClient).getId();
        }
        if (clientId == null) {
            return new RedirectView("/errorClientNameNull");
        }
        newOrder.setClientId(clientId);
        newOrder.setManagerId(getAuthenticationUserId());
        Long orderId = orderRepo.save(newOrder).getId();
        String staticFolderPath = "/Users/vladimirfilimonov/IdeaProjects/printingCRM/src/main/resources/static";
        saveScore(score, orderId, staticFolderPath);
        saveMaketList(meketList, orderId, staticFolderPath);
        saveBlankOrder(blankOrder, orderId, staticFolderPath);
        return new RedirectView("/");
    }

    private void saveScore(final MultipartFile score,
                           final Long orderId,
                           final String staticFolderPath) throws IOException {
        List<filesPath> filesData = filesPathRepo.findByOrderIdAndFileType(orderId, "scope");
        if (!filesData.isEmpty() && score.getBytes().length > 0) {
            deleteFile(filesData, staticFolderPath);
        }
        if (score.getBytes().length > 0) {
            String fileExtension = score.getOriginalFilename().substring(score.getOriginalFilename().lastIndexOf("."));
            String fileName = "Scope_" + orderId + fileExtension;
            Path scorePath = Paths.get(staticFolderPath+"/documents/score", fileName);
            Files.write(scorePath, score.getBytes());
            filesPath newFile = new filesPath(orderId, "scope", "/documents/score/"+fileName);
            filesPathRepo.save(newFile);
        }
    }

    private void saveMaketList(final MultipartFile[] meketList,
                               Long orderId,
                               String staticFolderPath) throws IOException {
        List<filesPath> filesData = filesPathRepo.findByOrderIdAndFileType(orderId, "layout");
        if (!filesData.isEmpty() && meketList.length >= 1) {
            deleteFile(filesData, staticFolderPath);
        }
        if (meketList[0].getBytes().length >= 1) {
            for (int i = 0; i < meketList.length; i++) {
                String fileExtension = meketList[i].getOriginalFilename().substring(meketList[i].getOriginalFilename().lastIndexOf("."));
                String fileName = "Layout_" + orderId + "_" + i+1 + fileExtension;
                Path meketListPath = Paths.get(staticFolderPath + "/documents/maket", fileName);
                Files.write(meketListPath, meketList[i].getBytes());
                filesPath newFile = new filesPath(orderId, "layout", "/documents/maket/" + fileName);
                filesPathRepo.save(newFile);
            }
        }
    }

    private void saveBlankOrder(final MultipartFile blankOrder,
                                  Long orderId,
                                  String staticFolderPath) throws IOException {
        List<filesPath> filesData = filesPathRepo.findByOrderIdAndFileType(orderId, "blank");
        if (!filesData.isEmpty() && blankOrder.getBytes().length > 0) {
            deleteFile(filesData, staticFolderPath);
        }
        if (blankOrder.getBytes().length > 0) {
            String fileExtension = blankOrder.getOriginalFilename().substring(blankOrder.getOriginalFilename().lastIndexOf("."));
            String fileName = "Blank_" + orderId + fileExtension;
            Path blankOrderPath = Paths.get(staticFolderPath+"/documents/blankOrder", fileName);
            Files.write(blankOrderPath, blankOrder.getBytes());
            filesPath newFile = new filesPath(orderId, "blank", "/documents/blankOrder/"+fileName);
            filesPathRepo.save(newFile);
        }
    }

    private void deleteFile(final List<filesPath> filesData, final String staticFolderPath) throws IOException {
        for (filesPath file : filesData) {
            Path path = Paths.get(staticFolderPath + file.getFileUrl());
            try {
                Files.delete(path);
                filesPathRepo.delete(file);
            } catch (IOException e) {
                throw new IOException("Ошибка при удалении файла: " + path, e);
            }
        }
    }

    @GetMapping("/info/{orderId}")
    public String getOrderInfoPage(Model model,
                                   @PathVariable("orderId") Long orderId,
                                   Principal principal) {
        String currentUsername = principal.getName();
        if (!hasAccessToOrder(orderId, currentUsername)) {
            return "error/error-403";
        }
        Orders order = orderRepo.findById(orderId).orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));
        model.addAttribute("orderInfo", order);
        List<filesPath> filesPathList = filesPathRepo.findByOrderIdAndFileType(orderId, "layout");
        model.addAttribute("filesPathList", filesPathList);
        model.addAttribute("clientCompany", clientRepo.findClientsById(order.getClientId()));
        return "orderInfo";
    }


    @GetMapping("/info/downloadFile/{orderId}/{fileType}")
    public ResponseEntity<Resource> getDownloadFile(@PathVariable("orderId") final Long orderId,
                                                    @PathVariable("fileType") final String fileType,
                                                    Model model,
                                                    Principal principal) throws IOException {
        String currentUsername = principal.getName();
        if (!hasAccessToOrder(orderId, currentUsername)) {
            return ResponseEntity.status(403).build(); // Возврат 403 статуса, если доступ запрещён
        }

        List<filesPath> filesPathList = filesPathRepo.findByOrderIdAndFileType(orderId, fileType);

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
                for (filesPath file : filesPathList) {
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

    private boolean hasAccessToOrder(Long orderId, String username) {
        Orders order = orderRepo.findOrdersById(orderId);
        if (order == null) {
            throw new IllegalArgumentException("Order not found: " + orderId);
        }
        return order.getManagerId().equals(username) ||
                employeeRepo.findByUsername(username).getAuthorities().stream()
                        .anyMatch(auth -> auth.getAuthority().equals("ROLE_OWNER"));
    }
}

