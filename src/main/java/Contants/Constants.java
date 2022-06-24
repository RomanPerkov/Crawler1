package Contants;

import org.openqa.selenium.By;

public class Constants {


    public static final String START_URL = "https://www.drive2.ru/cars/?sort=selling";


    public static final By ALL_MARKS_BUTTON = By.xpath("//button[@class='c-block__more']");

    public static final By ALL_CARS=By.xpath("//a[contains(@class,'c-link c-link--text')]");

    public static final By EMPTY_OR_NOT = By.xpath("//div[@class='o-grid o-grid--3 o-grid--equal']");

    public static final By BUTTON_SHOW_MORE = By.xpath("//button[@class='r-button-unstyled c-catalog-button']");

    public static final By LIST_OF_ALL_MODELS_OF_THIS_BRAND = By.xpath("//a[contains(@class,'u-link-area')]");


    public static class мehicleСharacteristics{

        public static final By MILLEAGE = By.xpath("//div[@class='c-car-forsale']/ul/li[1]");  // пробег

        public static final By ENGINE_CAPACITY =  By.xpath("//div[@class='c-car-forsale']/ul/li[2]");   // объем и мощность

        public static final By PRODUCTION_EAR = By.xpath("//div[@class='c-car-forsale']/ul/li[5]");     // год производста или покупки

        public static final By PRODUCTION_EAR2 = By.xpath("//div[@class='c-car-forsale']/ul/li[4]");     // год производста или покупки если электрокар

        public static final By BRAND = By.xpath("//div[@class='c-car-info']/div/a[2]"); // бренд авто

        public static final By MODEL = By.xpath("//div[@class='c-car-info']/div/a[3]");    // модель авто

        public static final By TRADE_ACCAUNT = By.xpath("//div[@class='c-user-card__body']/div/a/span");   // ник продавца

        public static final By PRICE = By.xpath("//div[@class='c-car-forsale__price']/strong"); // цена авто
    }


}
