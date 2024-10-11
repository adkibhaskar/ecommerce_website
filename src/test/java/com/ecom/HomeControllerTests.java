package com.ecom;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitForSelectorState;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


import java.nio.file.Paths;

@TestMethodOrder(OrderAnnotation.class)
public class HomeControllerTests {
    private static Playwright playwright;
    private static Browser browser;
    private static Page page;

    @BeforeAll
    public static void setUp() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
        page = browser.newPage(); 
    }

    @AfterAll
    public static void cleanUp() {
        browser.close();
        playwright.close();
    }

    @Test
    @Order(1)
    public void testIndexPage() throws Exception {
        page.navigate("http://localhost:8080/");
        String title = page.title();
        Assertions.assertEquals("Home Page", title);
        assertTrue(page.isVisible("img[alt='...'][src='img/ecom1.png']"), "Carousel image 1 is not visible");
        page.click(".carousel-control-next");
        page.waitForTimeout(2000);
        assertTrue(page.isVisible("img[alt='...'][src='img/ecom3.jpg']"), "Carousel image 2 is not visible");
        page.click(".carousel-control-next");
        page.waitForTimeout(2000);
        assertTrue(page.isVisible("img[alt='...'][src='img/ecom2.jpg']"), "Carousel image 3 is not visible");
        page.click(".carousel-control-prev");
        page.waitForTimeout(2000);
        assertTrue(page.isVisible("img[alt='...'][src='img/ecom3.jpg']"), "Carousel image 2 is not visible");
        page.click(".carousel-control-prev");
        page.waitForTimeout(2000);
        assertTrue(page.isVisible("img[alt='...'][src='img/ecom1.png']"), "Carousel image 1 is not visible");
        assertTrue(page.isVisible("p.text-center.fs-4:has-text('Category')"), "Category heading is not visible");
        int categoryCount = page.locator(".col-md-2").count();
        if (categoryCount > 0) {
            assertTrue(categoryCount > 0, "No category cards are rendered");
        } else {
            System.out.println("Category heading is visible but no category cards are rendered");
        }
        assertTrue(page.isVisible("p.text-center.fs-4:has-text('Latest Product')"), "Latest Product heading is not visible");
        int productCount = page.locator(".col-md-3").count();
        if (productCount > 0) {
            assertTrue(productCount > 0, "No product cards are rendered");
        } else {
            System.out.println("Latest Product heading is visible but no product cards are rendered");
        }
        Thread.sleep(2000); 
    }

    @Test
    @Order(2)
    public void testFailedLogin() throws Exception {
        page.click("a.login-btn");
        String title = page.title();
        assertEquals("Home Page", title); 
        page.fill("input[name='username']", "user@example.com");
        page.fill("input[name='password']", "wrongPassword");
        page.click("button[type='submit']");
        page.waitForTimeout(3000);  
        title = page.title();
        assertEquals("Home Page", title, "User is not on the login page after failed login attempt");
        assertTrue(page.isVisible("div.alert-danger"), "Error message is not displayed for failed login");
        assertTrue(page.textContent("div.alert-danger").contains("Email & password invalid"), "The error message does not show 'Bad credentials'");
        Thread.sleep(2000);
    }

    @Test
    @Order(3)
    public void testAdminRegister()throws Exception{
        page.navigate("http://localhost:8080/");
        page.click("a.register-admin-btn");
        assertTrue(page.isVisible("form#userRegister"), "Registration form is not visible");
        page.fill("input[name='name']", "Bhaskar Adki");
        page.fill("input[name='mobileNumber']", "9876543210");
        page.fill("input[name='email']", "adkibhaskar2002@gmail.com"); 
        page.fill("input[name='address']", "123 Main St");
        page.fill("input[name='city']", "City");
        page.fill("input[name='state']", "State");
        page.fill("input[name='pincode']", "123456");
        page.fill("input[name='password']", "Sai@1234");
        page.fill("input[name='confirmpassword']", "Sai@1234");
        page.setInputFiles("input[name='img']", Paths.get("/home/nityaobject/Downloads/pixels.jpg"));
        page.click("button[type='submit']");
        page.waitForTimeout(5000);
        assertTrue(page.isVisible("p.text-success"), "Success message is not visible");
        String successMessage = page.textContent("p.text-success");
        assertTrue(successMessage.contains("Register successfully"), "Success message does not match expected.");
        Thread.sleep(2000);
    }
    @Test
    @Order(4)
    public void testLoginAdminPage()throws Exception{
        page.click("a.login-btn");
        String title = page.title();
        assertEquals("Home Page", title);
        assertTrue(page.isVisible("form#userLogin"), "Login Form is not visible");
        assertTrue(page.isVisible("div.card-header p.text-center"), "Login header is not visible");
        assertTrue(page.isVisible("input[name='username']"), "Email input is not visible");
        assertTrue(page.isVisible("input[name='password']"), "Password input is not visible");
        assertTrue(page.isVisible("button[type='submit']"), "Login button is not visible");
        page.fill("input[name='username']", "adkibhaskar2002@gmail.com");
        page.fill("input[name='password']", "Sai@1234");
        page.click("button[type='submit']");
        String currentUrl = page.url();
        assertEquals("http://localhost:8080/admin/", currentUrl, "User is not redirected to the admin page after successful login");
        Thread.sleep(2000);
    }
    @Test
    @Order(5)
    public void testAddCategoryFunctionality()throws Exception{
        page.click("div.container div.row div.col-md-4 a.add-category");
        assertTrue(page.isVisible("p.fs-4"), "Add Category header is not visible");
        assertTrue(page.isVisible("input[name='name']"), "Category name input is not visible");
        assertTrue(page.isVisible("input[name='isActive']"), "Status radio buttons are not visible");
        assertTrue(page.isVisible("input[name='file']"), "Upload Image input is not visible");
        assertTrue(page.isVisible("button.btn.btn-primary.col-md-12.mt-2"), "Save button is not visible");
        page.fill("input[name='name']", "Sample Category");
        page.check("input[name='isActive'][value='true']");
        page.setInputFiles("input[name='file']", Paths.get("/home/nityaobject/Downloads/pixels.jpg"));
        page.click("button[type='submit']");
        page.waitForTimeout(5000);
        assertTrue(page.isVisible("p.text-success"), "Success message is not visible");
        String successMessage = page.textContent("p.text-success");
        assertTrue(successMessage.contains("Saved successfully"), "Success message does not match expected.");
        assertTrue(page.isVisible("table"), "Category table is not visible");
        assertTrue(page.isVisible("table thead tr th:has-text('Sl No')"), "'Sl No' column header is not visible");
        assertTrue(page.isVisible("table thead tr th:has-text('Category')"), "'Category' column header is not visible");
        assertTrue(page.isVisible("table thead tr th:has-text('Status')"), "'Status' column header is not visible");
        assertTrue(page.isVisible("table thead tr th:has-text('Image')"), "'Image' column header is not visible");
        assertTrue(page.isVisible("table thead tr th:has-text('Action')"), "'Action' column header is not visible");
        assertTrue(page.isVisible("table tbody tr:first-child th[scope='row']"), "Sl No field in first row is not visible");
        assertTrue(page.isVisible("table tbody tr:first-child td:has-text('Category')"), "Category field in first row is not visible");
        assertTrue(page.isVisible("table tbody tr:first-child td:has-text('true')"), "Status field in first row is not visible");
        assertTrue(page.isVisible("table tbody tr:first-child td img"), "Image field in first row is not visible");
        assertTrue(page.isVisible("table tbody tr:first-child td a.btn-primary"), "Edit button in first row is not visible");
        assertTrue(page.isVisible("table tbody tr:first-child td a.btn-danger"), "Delete button in first row is not visible");
        int tableRowCount = page.locator("table tbody tr").count();
        String totalCategoryText = page.textContent("div.col-md-4:has-text('Total Category')");
        int totalCategoryCount = Integer.parseInt(totalCategoryText.split(":")[1].trim());
        assertEquals(tableRowCount, totalCategoryCount, "The total category count does not match the number of rows in the table");
        Thread.sleep(2000);
    }
    @Test
    @Order(6)
    public void testAddProductFunctionality()throws Exception{
        page.navigate("http://localhost:8080/admin/");
        page.click("div.container div.row div.col-md-4 a.add-product");
        assertTrue(page.isVisible("p.fs-4"), "Add Product header is not visible");
        assertTrue(page.isVisible("input[name='title']"), "Title input is not visible");
        assertTrue(page.isVisible("textarea[name='description']"), "Description textarea is not visible");
        assertTrue(page.isVisible("select[name='category']"), "Category select is not visible");
        assertTrue(page.isVisible("input[name='price']"), "Price input is not visible");
        assertTrue(page.isVisible("input[name='stock']"), "Stock input is not visible");
        assertTrue(page.isVisible("input[name='file']"), "Upload Image input is not visible");
        assertTrue(page.isVisible("button[type='submit']"), "Submit button is not visible");
        page.fill("input[name='title']", "Sample Product");
        page.fill("textarea[name='description']", "This is a sample product description.");
        // page.click("select[name='category']");
        page.waitForSelector("select[name='category']", new Page.WaitForSelectorOptions().setState(WaitForSelectorState.VISIBLE));
        page.selectOption("select[name='category']", "Sample Category");
        page.fill("input[name='price']", "500");
        page.fill("input[name='stock']", "100");
        page.check("input[name='isActive'][value='true']");
        page.setInputFiles("input[name='file']", Paths.get("/home/nityaobject/Downloads/pixels.jpg")); 
        page.click("button[type='submit']");
        page.waitForTimeout(5000);
        assertTrue(page.isVisible("p.text-success"), "Success message is not visible");
        String successMessage = page.textContent("p.text-success");
        System.out.println("The success Message is : " + successMessage);
        assertTrue(successMessage.contains("Product Saved Success"), "Success message does not match expected.");
        Thread.sleep(2000);
    }
    @Test
    @Order(7)
    public void testEditCategoryFunctionality(){
        page.navigate("http://localhost:8080/admin/");
        page.click("div.container div.row div.col-md-4 a.add-category");
        assertTrue(page.isVisible("table"), "Category table is not visible");
        assertTrue(page.isVisible("table thead tr th:has-text('Action')"), "'Action' column header is not visible");
        assertTrue(page.isVisible("table tbody tr:first-child td a.btn-primary"), "Edit button in first row is not visible");
        page.click("table tbody tr:first-child td a.btn-primary");
        assertTrue(page.isVisible("p.fs-4"), "Edit Category header is not visible");
        assertTrue(page.isVisible("input[name='name']"), "Category name input is not visible");
        assertTrue(page.isVisible("input[name='isActive'][value='true']"), "Active radio button is not visible");
        assertTrue(page.isVisible("input[name='isActive'][value='false']"), "Inactive radio button is not visible");
        assertTrue(page.isVisible("input[name='file']"), "Upload Image input is not visible");
        assertTrue(page.isVisible("button.btn.btn-primary.col-md-12.mt-2"), "Update button is not visible");
        page.fill("input[name='name']", "Updated Category");
        page.check("input[name='isActive'][value='true']");
        page.setInputFiles("input[name='file']", Paths.get("/home/nityaobject/Downloads/pixels.jpg"));
        page.click("button[type='submit']");
        page.waitForTimeout(5000);
        assertTrue(page.isVisible("p.text-success"), "Success message is not visible");
        String successMessage = page.textContent("p.text-success");
        assertTrue(successMessage.contains("Category update success"), "Success message does not match expected.");
    }
    @Test
    @Order(8)
    public void testDeleteCategoryFunctionality(){
        page.navigate("http://localhost:8080/admin/");
        page.click("div.container div.row div.col-md-4 a.add-category");
        assertTrue(page.isVisible("table"), "Category table is not visible");
        assertTrue(page.isVisible("table thead tr th:has-text('Action')"), "'Action' column header is not visible");
        assertTrue(page.isVisible("table tbody tr:first-child td a.btn-danger"), "Delete button in first row is not visible");
        page.click("table tbody tr:first-child td a.btn-danger");
        page.waitForTimeout(5000);
        int tableRowCount = page.locator("table tbody tr").count();
        String totalCategoryText = page.textContent("div.col-md-4:has-text('Total Category')");
        int totalCategoryCount = Integer.parseInt(totalCategoryText.split(":")[1].trim());
        assertEquals(tableRowCount, totalCategoryCount, "The total category count does not match the number of rows in the table");
    }
    @Test
    @Order(9)
    public void testAddAdminFunctionality()throws Exception{
        page.navigate("http://localhost:8080/admin/");
        page.click("div.container div.row div.col-md-4 a.add-admin");
        assertTrue(page.isVisible("p.fs-4.text-center"), "Add Admin header is not visible");
        assertTrue(page.isVisible("input[name='name']"), "Full Name input is not visible");
        assertTrue(page.isVisible("input[name='mobileNumber']"), "Mobile Number input is not visible");
        assertTrue(page.isVisible("input[name='email']"), "Email input is not visible");
        assertTrue(page.isVisible("input[name='address']"), "Address input is not visible");
        assertTrue(page.isVisible("input[name='city']"), "City input is not visible");
        assertTrue(page.isVisible("input[name='state']"), "State input is not visible");
        assertTrue(page.isVisible("input[name='pincode']"), "Pincode input is not visible");
        assertTrue(page.isVisible("input[name='password']"), "Password input is not visible");
        assertTrue(page.isVisible("input[name='cpassword']"), "Confirm Password input is not visible");
        assertTrue(page.isVisible("input[name='img']"), "Profile Image input is not visible");
        assertTrue(page.isVisible("button[type='submit']"), "Register button is not visible");
        page.fill("input[name='name']", "Atharv Adki");
        page.fill("input[name='mobileNumber']", "9876543210");
        page.fill("input[name='email']", "adkiatharv@gmail.com");
        page.fill("input[name='address']", "1234 Elm Street");
        page.fill("input[name='city']", "City");
        page.fill("input[name='state']", "State");
        page.fill("input[name='pincode']", "10001");
        page.fill("input[name='password']", "Password123");
        page.fill("input[name='cpassword']", "Password123");
        page.setInputFiles("input[name='img']", Paths.get("/home/nityaobject/Downloads/pixels.jpg"));
        page.click("button[type='submit']");
        page.waitForTimeout(5000);
        assertTrue(page.isVisible("p.text-success"), "Success message is not visible");
        String successMessage = page.textContent("p.text-success");
        assertTrue(successMessage.contains("Register successfully"), "Success message does not match expected.");
        Thread.sleep(2000);
    }

    @Test
    @Order(10)
    public void testUsersDataDisplayFunctionality()throws Exception{
        page.navigate("http://localhost:8080/admin/");
        page.click("div.container div.row div.col-md-4 a.users-data");
        String userType = page.textContent("div.card-header p.fs-4");
        System.out.println("The UserType is : " + userType);
        assertNotNull(userType);
        assertTrue(userType.equals("Users"));
        assertTrue(page.isVisible("th:has-text('Sl No')"));
        assertTrue(page.isVisible("th:has-text('Profile')"));
        assertTrue(page.isVisible("th:has-text('Name')"));
        assertTrue(page.isVisible("th:has-text('Email')"));
        assertTrue(page.isVisible("th:has-text('Mobile No')"));
        assertTrue(page.isVisible("th:has-text('Address')"));
        assertTrue(page.isVisible("th:has-text('Status')"));
        assertTrue(page.isVisible("th:has-text('Action')"));
        Thread.sleep(2000);
    }

    @Test
    @Order(11)
    public void testAdminDataDisplayFunctionality()throws Exception{
        page.navigate("http://localhost:8080/admin/");
        page.click("div.container div.row div.col-md-4 a.admin-data");
        String userType = page.textContent("div.card-header p.fs-4");
        System.out.println("The UserType is : " + userType);
        assertNotNull(userType);
        assertTrue(userType.equals("Admin"));
        assertTrue(page.isVisible("th:has-text('Sl No')"));
        assertTrue(page.isVisible("th:has-text('Profile')"));
        assertTrue(page.isVisible("th:has-text('Name')"));
        assertTrue(page.isVisible("th:has-text('Email')"));
        assertTrue(page.isVisible("th:has-text('Mobile No')"));
        assertTrue(page.isVisible("th:has-text('Address')"));
        assertTrue(page.isVisible("th:has-text('Status')"));
        assertTrue(page.isVisible("th:has-text('Action')"));
        assertTrue(page.isVisible("tbody tr"));
        Thread.sleep(2000);
    }
    @Test
    @Order(12)
    public void testProductDetailsPage()throws Exception{
        page.navigate("http://localhost:8080/admin/");
        page.click("div.container div.row div.col-md-4 a.product-data");
        assertTrue(page.isVisible("th:has-text('Sl No')"), "Column 'Sl No' is not visible.");
        assertTrue(page.isVisible("th:has-text('Image')"), "Column 'Image' is not visible.");
        assertTrue(page.isVisible("th:has-text('Title')"), "Column 'Title' is not visible.");
        assertTrue(page.isVisible("th:has-text('Category')"), "Column 'Category' is not visible.");
        assertTrue(page.isVisible("th:has-text('Price')"), "Column 'Price' is not visible.");
        assertTrue(page.isVisible("th:has-text('Discount')"), "Column 'Discount' is not visible.");
        assertTrue(page.isVisible("th:has-text('Discount Price')"), "Column 'Discount Price' is not visible.");
        assertTrue(page.isVisible("th:has-text('Status')"), "Column 'Status' is not visible.");
        assertTrue(page.isVisible("th:has-text('Stock')"), "Column 'Stock' is not visible.");
        assertTrue(page.isVisible("th:has-text('Action')"), "Column 'Action' is not visible.");
        assertTrue(page.isVisible("tbody tr"), "No user rows found in the table.");
        String totalCountText = page.textContent("div.col-md-4.total-products");
        String number = totalCountText.replaceAll("[^0-9]", "").trim(); 
        int totalCount = Integer.parseInt(number);
        int tableRowCount = page.querySelectorAll("tbody tr").size();
        assertEquals(totalCount, tableRowCount, "Total count does not match the number of users displayed in the table.");
        Thread.sleep(2000); 
    }
    @Test
    @Order(13)
    public void testAdminLogoutFunctionality()throws Exception{
        page.navigate("http://localhost:8080/admin/");
        page.click("a.nav-link.dropdown-toggle.user-dropdown"); 
        page.click("ul.dropdown-menu li a.dropdown-item.logout-btn");
        page.waitForURL("http://localhost:8080/signin?logout");
        String title = page.title();
        assertEquals("Home Page", title, "Admin is not redirected to the login page after logout");
        assertTrue(page.isVisible("div.alert-success"), "Logout success message is not displayed.");
        assertTrue(page.isVisible("text=Logout Sucessfully"), "The logout success message text is incorrect.");
        Thread.sleep(2000);

    }

    @Test
    @Order(14)
    public void testSuccessfulRegistration() throws Exception {
        page.click("a.register-user-btn");
        assertTrue(page.isVisible("form#userRegister"), "Registration form is not visible");
        page.fill("input[name='name']", "John Doe");
        page.fill("input[name='mobileNumber']", "9876543210");
        page.fill("input[name='email']", "john.doe@example.com"); 
        page.fill("input[name='address']", "123 Main St");
        page.fill("input[name='city']", "City");
        page.fill("input[name='state']", "State");
        page.fill("input[name='pincode']", "123456");
        page.fill("input[name='password']", "password123");
        page.fill("input[name='confirmpassword']", "password123");
        page.setInputFiles("input[name='img']", Paths.get("/home/nityaobject/Downloads/pixels.jpg"));
        page.click("button[type='submit']");
        page.waitForTimeout(5000);
        assertTrue(page.isVisible("p.text-success"), "Success message is not visible");
        String successMessage = page.textContent("p.text-success");
        assertTrue(successMessage.contains("Register successfully"), "Success message does not match expected.");
        Thread.sleep(2000);
    }

    @Test
    @Order(15)
    public void testRegistrationWithExistingEmail() throws Exception {
        page.click("a.register-user-btn");
        assertTrue(page.isVisible("form#userRegister"), "Registration form is not visible");
        page.fill("input[name='name']", "Jane Doe");
        page.fill("input[name='mobileNumber']", "1234567890");
        page.fill("input[name='email']", "john.doe@example.com"); 
        page.fill("input[name='address']", "456 Main St");
        page.fill("input[name='city']", "City");
        page.fill("input[name='state']", "State");
        page.fill("input[name='pincode']", "654321");
        page.fill("input[name='password']", "password123");
        page.fill("input[name='confirmpassword']", "password123");
        page.setInputFiles("input[name='img']", Paths.get("/home/nityaobject/Downloads/pixels.jpg"));
        page.click("button[type='submit']");
        page.waitForTimeout(5000);
        assertTrue(page.isVisible("p.text-danger"), "Error message is not visible");
        String errorMessage = page.textContent("p.text-danger");
        assertTrue(errorMessage.contains("Email already exist"), "Error message does not match expected.");
        Thread.sleep(2000);
    }
    @Test
    @Order(16)
    public void testLoginPage()throws Exception{
        page.click("a.login-btn");
        String title = page.title();
        assertEquals("Home Page", title);
        assertTrue(page.isVisible("form#userLogin"), "Login Form is not visible");
        assertTrue(page.isVisible("div.card-header p.text-center"), "Login header is not visible");
        assertTrue(page.isVisible("input[name='username']"), "Email input is not visible");
        assertTrue(page.isVisible("input[name='password']"), "Password input is not visible");
        assertTrue(page.isVisible("button[type='submit']"), "Login button is not visible");
        page.fill("input[name='username']", "john.doe@example.com");
        page.fill("input[name='password']", "password123");
        page.click("button[type='submit']");
        title = page.title();
        assertEquals("Home Page", title, "User is not redirected to the Home Page after successful login");
        String currentUrl = page.url();
        assertEquals("http://localhost:8080/", currentUrl, "User is not redirected to the user page after successful login");
        Thread.sleep(2000);
    }
    @Test
    @Order(17)
    public void testSingleProductDetailsPage()throws Exception{
        page.click("div.col-md-3 a.product-link");
        String currentUrl = page.url();
        assertTrue(currentUrl.matches(".*/product/\\d+$"), "Redirection to product page failed. URL does not match expected pattern.");
        assertTrue(page.isVisible("img[alt='']"), "Product image is not visible");
        assertTrue(page.isVisible("p.fs-3"), "Product title is not visible");
        assertTrue(page.isVisible("span.fw-bold:has-text('Description :')"), "Product description is not visible");
        assertTrue(page.isVisible("span.fw-bold:has-text('Product Details:')"), "Product details section is not visible");
        boolean isAvailable = page.isVisible("span.badge.bg-success");
        boolean isOutOfStock = page.isVisible("span.badge.bg-warning");
        assertTrue(isAvailable || isOutOfStock, "Product availability status is not visible");
        assertTrue(page.isVisible("p.fs-5.fw-bold"), "Product price and discount are not visible");
        assertTrue(page.isVisible("i.fas.fa-money-bill-wave"), "Cash On Delivery icon is not visible");
        assertTrue(page.isVisible("i.fas.fa-undo-alt"), "Return Available icon is not visible");
        assertTrue(page.isVisible("i.fas.fa-truck-moving"), "Free Shipping icon is not visible");
        if (isAvailable) {
            assertTrue(page.isVisible("a.btn.btn-danger.col-md-12"), "Add to Cart button is not visible when product is available");
            page.click("a.btn.btn-danger.col-md-12");
            page.waitForSelector("p.text-success");
            assertTrue(page.isVisible("p.text-success"), "Success message is not visible");
            String successMessage = page.textContent("p.text-success");
            assertTrue(successMessage.contains("Product added to cart"), "Success message does not match expected text");
        }else {
            assertTrue(page.isVisible("a.btn.btn-warning.col-md-12"), "Out of Stock button is not visible");
        }
    }

    @Test
    @Order(18)
    public void testCartPageFieldsPresence(){
        page.navigate("http://localhost:8080/");
        page.click("ul.navbar-nav li.nav-item a.nav-link.cart-btn");
        String currentUrl = page.url();
        assertEquals("http://localhost:8080/user/cart", currentUrl, "User is not redirected to the cart page on clicking cart button");
        assertTrue(page.isVisible("th:has-text('Sl No')"), "Sl No column is not visible");
        assertTrue(page.isVisible("th:has-text('Image')"), "Image column is not visible");
        assertTrue(page.isVisible("th:has-text('Product Name')"), "Product Name column is not visible");
        assertTrue(page.isVisible("th:has-text('Price')"), "Price column is not visible");
        assertTrue(page.isVisible("th:has-text('Quantity')"), "Quantity column is not visible");
        assertTrue(page.isVisible("th:has-text('Total Price')"), "Total Price column is not visible");
    }
    @Test
    @Order(19)
    public void testQuantityUpdateAndPriceCalculation()throws Exception {
        String initialQuantityStr = page.textContent("td.text-center");
        int initialQuantity = Integer.parseInt(initialQuantityStr.replaceAll("[^0-9]", "").trim());
        String initialTotalPriceStr = page.textContent("td.fw-bold:has-text('Total Price') + td"); // Total price column
        double initialTotalPrice = Double.parseDouble(initialTotalPriceStr.replace("₹", "").trim());
        page.click("a:has(i.fa-solid.fa-plus)");
        page.waitForTimeout(1000); 
        String newQuantityStr = page.textContent("td.text-center");
        int newQuantity = Integer.parseInt(newQuantityStr.replaceAll("[^0-9]", "").trim());
        String newTotalPriceStr = page.textContent("td.fw-bold:has-text('Total Price') + td");
        double newTotalPrice = Double.parseDouble(newTotalPriceStr.replace("₹", "").trim());
        assertEquals(initialQuantity + 1, newQuantity, "Quantity did not increase correctly");
        assertTrue(newTotalPrice > initialTotalPrice, "Total price did not increase correctly");
        page.click("a:has(i.fa-solid.fa-minus)");
        page.waitForTimeout(1000);
        String decreasedQuantityStr = page.textContent("td.text-center");
        int decreasedQuantity = Integer.parseInt(decreasedQuantityStr.replaceAll("[^0-9]", "").trim());
        String decreasedTotalPriceStr = page.textContent("td.fw-bold:has-text('Total Price') + td");
        double decreasedTotalPrice = Double.parseDouble(decreasedTotalPriceStr.replace("₹", "").trim());
        assertEquals(initialQuantity, decreasedQuantity, "Quantity did not decrease correctly");
        assertEquals(initialTotalPrice, decreasedTotalPrice, "Total price did not decrease correctly");
        Thread.sleep(2000);
    }
    
    @Test
    @Order(20)
    public void testLogoutPage()throws Exception{
        page.navigate("http://localhost:8080/");
        page.click("a.nav-link.dropdown-toggle.user-dropdown"); 
        page.click("ul.dropdown-menu li a.dropdown-item.logout-btn");
        page.waitForURL("http://localhost:8080/signin?logout");
        String title = page.title();
        assertEquals("Home Page", title, "User is not redirected to the login page after logout");
        assertTrue(page.isVisible("div.alert-success"), "Logout success message is not displayed.");
        assertTrue(page.isVisible("text=Logout Sucessfully"), "The logout success message text is incorrect.");
        Thread.sleep(2000);
    }
    
    
    
}

