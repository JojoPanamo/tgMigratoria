package com.example.tgMigratoria.service;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;


import java.time.Duration;
import java.util.List;
@Component
public class MigrationStatusScrapper {
    private static final String MIGRATION_SERVICE_URL = "https://www.migraciones.gov.ar/accesible/consultaTramitePrecaria/ConsultaUnificada.php"; // Замените на реальный URL

    public String getStatusForUser(String documentNumber, String birthDay, String birthMonth, String birthYear) throws InterruptedException {
        System.setProperty("webdriver.chrome.driver", "/Users/jojomac/Desktop/progProjects/tgMigratoria/chromedriver");
        // Создание экземпляра веб-драйвера (Chrome)
        //WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();

        try {
            // Переход на страницу миграционной службы
            driver.get(MIGRATION_SERVICE_URL);

            // Взаимодействие с элементами страницы (например, ввод номера дела)
            WebElement documentNumberInput = driver.findElement(By.id("expediente")); // Замените на реальный ID
            WebElement birthDayInput = driver.findElement(By.id("dia"));
            WebElement birthMonthInput = driver.findElement(By.id("mes"));
            WebElement birthYearInput = driver.findElement(By.id("anio"));
            documentNumberInput.sendKeys(documentNumber);
            birthDayInput.sendKeys(birthDay);
            birthMonthInput.sendKeys(birthMonth);
            birthYearInput.sendKeys(birthYear);
//
            Thread thread = new Thread();
            thread.sleep(300);
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
            WebElement clickSubmit = wait.until(ExpectedConditions.elementToBeClickable(By.id("buscar_datos")));
            clickSubmit.click();
            // Дополнительные действия (например, клик по кнопке для отправки данных)
            WebDriverWait wait1 = new WebDriverWait(driver, Duration.ofSeconds(20));
            wait1.until(ExpectedConditions.visibilityOfElementLocated(By.id("datos_respuesta")));

            // Ожидание загрузки страницы и получение статуса дела
            WebElement divElement = driver.findElement(By.cssSelector("div.circle.active"));
            Thread thread1 = new Thread();
            thread1.sleep(3000);

            List<WebElement> spanElements = divElement.findElements(By.cssSelector("span[id^='span_txt_paso_7_aux_']"));
            String statusText = "";

            for (WebElement spanElement : spanElements) {
                statusText = spanElement.getText();
                System.out.println(statusText);
            }
            return "Статус вашего дела: " + statusText;
        } finally {
            // Закрытие веб-драйвера после использования
           driver.quit();
        }
    }
}
