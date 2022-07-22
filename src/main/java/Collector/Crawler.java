package Collector;


import entity.car;
import entity.href;
import io.github.bonigarcia.wdm.WebDriverManager;


import org.apache.log4j.Logger;


import org.hibernate.query.Query;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.lang.NullPointerException;

import jakarta.persistence.NoResultException;

import static Contants.Constants.*;

public class Crawler {

    protected final static Logger logger = Logger.getLogger(Crawler.class);

    static List<WebElement> listAllMarks;       // коллекция хранящая в себе список всех WebElemet авто

    public static List<String> linksToModelsOfAllCars = new ArrayList<>();      // коллекция хранящая в себе список ссылок href на все марки авто( т е  в коллекции количесвто марок а не все машины на сайте)

    static WebDriver driver;


    /**
     * Этот метод создает вебдрайвер.
     */
    public WebDriver createWebDriver() {
        if (driver == null) {
            try {
                WebDriverManager.chromedriver().setup();
                logger.info("Creating webDriver");
                driver = new ChromeDriver();
            } catch (Exception e) {
                logger.info("something happened at the stage of creating a web driver");
                e.printStackTrace();
            }
        }
        return driver;
    }

    /**
     * Этот метод заходит на стартовую страницу сервиса,
     * показывает все марки автомобилей, создает список всех автомобилей,
     * затем метод проходит по списку, переходя к
     * вкладке каждой марки автомобиля и добавлет туда все ссылки на  автомобили
     * в базу данных.
     */
    public void scanning() {


        createWebDriver();


        LocalDateTime time = LocalDateTime.now();

        driver.navigate().to(START_URL);                              // приходим на стартовую страницу
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));   // пока что устанавливаем неявное ожидание
        driver.findElement(ALL_MARKS_BUTTON).click();                 // нажимаем кнопку для раскрытия всех марок авто на главной странице
        listAllMarks = driver.findElements(ALL_CARS);// получаем полный список всех элементов марок авто
        for (WebElement element : listAllMarks) {
            try {
                String href = element.getAttribute("href");
                linksToModelsOfAllCars.add(href);       // получаем коллекцию ссылок на все марки авто
                logger.info("car link was added successfully");
            } catch (Exception e) {
                logger.info("something happened" + element + "----- " + e);    // логируем
                continue;
            }
        }


        int count = 0;
        List<WebElement> temporaryCollection;    // создаем временную коллекцию для хранения ссылок на авто

        for (int i = 0; i < linksToModelsOfAllCars.size() - 1; i++) {                                    //цикл который перебирает разные марки ( каждый новый цикл, новаямарка, если она есть)
            String href = linksToModelsOfAllCars.get(i);                                              // берет ссылку авто  из коллекции
            driver.navigate().to(href);                                                     // переходит по ней
            String carBrand = driver.findElement(By.xpath("//title")).getAttribute("innerText");
            logger.info("enter brand page " + carBrand);                                      // логируем вход
            if (driver.findElements(EMPTY_OR_NOT).size() > 0) {                           //ищет элемент таблицу в которой расположены элементы авто(если есть запускается цикл while), если нет запускается цикл следующий марки)
                logger.info("Login to the page of the car brand is successful, I'm starting to collect items.");
                while (driver.findElements(BUTTON_SHOW_MORE).size() != 0) {                                    //цикл который прокручивает страницу с авто до конца(проверяет есть ли кнопка показать бюльше)
                    logger.info("Start cycle button");
                    JavascriptExecutor jse = (JavascriptExecutor) driver;                               // используем объект ДжаваСкрипт для скролла страницы вниз
                    jse.executeScript("window.scrollBy(0,50000)", "");
                    logger.info("Data is being collected, scrolling everything " + count);
                    count++;
                }                                                                                       // когда скроллинг страницы будет завершен, цикл прекращается
                count = 0;
                temporaryCollection = (driver.findElements(LIST_OF_ALL_MODELS_OF_THIS_BRAND));              // создаем временную коллекцию для хранения ссылок на авто
                addToDataBase(temporaryCollection);
                driver.navigate().to(START_URL);
                logger.info(carBrand + " brand data collection successfully completed");


            } else {
                logger.info("No " + carBrand + " at the moment " + LocalDateTime.now() + " not found");     //логируем факт того, что не было найдено ни одной машины этой марки
                driver.navigate().to(START_URL);                                                            // возврат к стартовому URL
                continue;
            }
        }


        LocalDateTime time1 = LocalDateTime.now();
        System.out.println(time);
        System.out.println(time1);


    }

    /**
     * Этот метод создает объекты ссылок на авто и записывает их в БД
     *
     * @param car
     */
    public void addToDataBase(List<WebElement> car) {
        SessionFactory factory = null;
        Session session = null;
        try {
            factory = new Configuration().configure("hibernate.cfg.xml")
                    .addAnnotatedClass(href.class)                                          // создаем сессию , логируем  провал
                    .buildSessionFactory();
            session = null;
            session = factory.getCurrentSession();
        } catch (HibernateException e) {
            logger.info(" something happened at the stage of creating a session with the database " + e + " method name " + "addToBD");
            e.printStackTrace();
        }


        try {
            session.beginTransaction();
            href href;
            for (WebElement s : car) {                              // создаем форич цикл и передаем в него список ссылок на авто
                href = new href(s.getAttribute("href"));        // создает объект ссылку
                session.persist(href);                              // добавляет объект ссылку в БД
            }
            session.getTransaction().commit();
        } catch (HibernateException e) {
            logger.info("something happened at the stage of saving data to the database " + e + " method name " + " addToBD()");
            e.printStackTrace();
        } finally {
            session.close();
            factory.close();
        }

    }

    /**
     * Этот метод получает в параметры ссылку на авто , переходит по мней , собирает данные о авто и создает
     * объект авто, возвращает этот объект.
     */
    public car collector(href link) {
        createWebDriver();
        System.out.println(link.getHref());
        String linkHref = link.getHref();
        car car;
        String productionEar;
        String price;
        String brand;
        String model;
        String engineCapacity;
        String milleage;
        String tradeAccount;
        driver.get(linkHref);                                                                       // переход по ссылке


        try {
            productionEar = driver.findElement(мehicleСharacteristics.PRODUCTION_EAR).getText();        // сбор информации о даты выпуска
        } catch (NoSuchElementException n) {
            try {


                productionEar = driver.findElement(мehicleСharacteristics.PRODUCTION_EAR2).getText();       // на случай если электрокар
            } catch (NoSuchElementException d) {
                productionEar = "null";
            }
        }

        try {
            price = driver.findElement(мehicleСharacteristics.PRICE).getText();                         // сбор информации о цене ( считывается та валюта в которой трейдер продает авто)
        } catch (NoSuchElementException d) {
            price = "null";
        }

        try {
            brand = driver.findElement(мehicleСharacteristics.BRAND).getAttribute("innerHTML");   // сбор информации о бренде
        } catch (NoSuchElementException d) {
            brand = "null";
        }
        try {
            model = driver.findElement(мehicleСharacteristics.MODEL).getAttribute("innerHTML");   // сбор информации о модели
        } catch (NoSuchElementException d) {
            model = "null";
        }

        try {
            engineCapacity = driver.findElement(мehicleСharacteristics.ENGINE_CAPACITY).getText();      // сбор информации о обьеме и лошадиных силах двигателя
        } catch (NoSuchElementException d) {
            engineCapacity = "null";
        }
        try {
            milleage = driver.findElement(мehicleСharacteristics.MILLEAGE).getText();                   // сбор информации о пробеге
        } catch (NoSuchElementException d) {
            milleage = "null";
        }
        try {
            tradeAccount = driver.findElement(мehicleСharacteristics.TRADE_ACCAUNT).getText();          // сбор информации  имени пользователя продавца
        } catch (NoSuchElementException d) {
            tradeAccount = "null";
        }
        car = new car(linkHref, productionEar, price, brand, model, engineCapacity, milleage, tradeAccount);
        return car;
    }

    /**
     * Этот метод запрашивает у БД объекты ссылки, проходит по ним собирает инфрмацию ,
     * создает объект авто и помещает ее в таблицу объектьов авто в БД
     */
    public void createObjectsCarInTableDataBase() {
        SessionFactory factory = null;
        Session session = null;
        int count = 1;
        car car = null;


        while (true) {                                                              // запускаем цикл сбора ( проход по каждой ссылке и сбор информации) пока не закончится список
            try {

                factory = new Configuration().configure("hibernate.cfg.xml")
                        .addAnnotatedClass(entity.href.class)
                        .addAnnotatedClass(entity.car.class)
                        .buildSessionFactory();

                session = factory.getCurrentSession();
                session.beginTransaction();

                href href1 = session.get(href.class, String.valueOf(count));                                                // запрос у БД объекта сссылки
                car = collector(href1);                                                                                     // сбор и создание объекта авто
                session.persist(car);                                                                                   // заявка на  сохранение в БД
                session.delete(href1);
                session.getTransaction().commit();                                                                      // выполнение транзакции БД
                count++;                                                                                                // увеличиваем число пропуска считываемых ссылок на +1 ( для продвижения по списку)

                logger.info("machine with id= " + count + 1 + " was successfully added to the database");       // логируем успешное добавление


            } catch (HibernateException e) {
                logger.info("something happened at the stage of saving data to the database " + e);
                e.printStackTrace();
                try {
                    Thread.sleep(20000);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
                continue;
            } catch (NoResultException a) {                   // когда заканчиввается список ссылок на авто прекращаем цикл
                logger.info("end list");
                break;
            } catch (WebDriverException z) {
                logger.info("for some reason, the link could not be followed, the link may no longer exist" + car.getHref());        // когда не удаеться подключится к URL
                count++;
                continue;
            } catch (NullPointerException n) {
                logger.info("There is no such identifier, we go further");
                count++;
                continue;

            } finally {
                session.close();
                factory.close();
            }

        }


    }


    /**
     * Этот метод обращается к обновленной таблице сущностей ссылок на авто. Метод выявляет новые ссылки и создает и добавляет на их основе новые сущности машины в БД.
     * Метод выявляет устаревшие ссылки которых больше нет на сервисе и удаляет сущности авто с этими ссылками.
     */
    public void removingInvalidAndAddNewLinksFromDataBase() {
        car car = null;
        String tempHref = "";
        SessionFactory factory;
        Session session;
        int count = 1;
        String id = "";
        href href = null;
        List<href> temp = new ArrayList<>();
        List<String> idNullList = new ArrayList<>();


        factory = new Configuration().configure("hibernate.cfg.xml")
                .addAnnotatedClass(href.class)
                .addAnnotatedClass(car.class)
                .buildSessionFactory();
        session = factory.getCurrentSession();
        session.beginTransaction();


/**
 * обращается к БД и сравнивает новую таблицу href со значениями href в таблице car,
 * если в таблшице car отстутсвет машина с href который есть в таблице href то такой href добавляется
 * во временную коллекцию temp для последующего создания сущности car
 */
        while (true) {
            try {
                href = session.get(href.class, String.valueOf(count));
                tempHref = href.getHref();
                Query query = session.createQuery("select href from car where href=:tempHref");
                query.setParameter("tempHref", tempHref);
                System.out.println(query.getSingleResult());
                count++;

            } catch (NoResultException e) {
                temp.add(href);
                logger.info("new link found, add to temporary collection");
                count++;
                continue;
            } catch (NullPointerException d) {
                System.out.println("END LIST");
                break;
            }
        }
        session.close();
        System.out.println(temp);


/** тут наоборот сравниваются значения таблицы car, берется каждая сущность car и поле
 *  href этой сущности сравивается с таблицей href,
 *  если в таблице нет такого href значит car не акутальна
 * и все поля (кроме id) сущности car объявляются null. id строк машин которых объявили null
 * отправляются в коллекцию idNullList,
 * если в сервсие появились новые машины в эти id буудт записаные данные новых сущностей машин
 */

        count = 1;


        while (true) {
            try {

                session = factory.getCurrentSession();
                session.beginTransaction();
                //href = session.get(href.class, String.valueOf(count));
                car = session.get(car.class, String.valueOf(count));
                tempHref = car.getHref();
                id = car.getId();
                Query query = session.createQuery("select href from href where href=:tempHref");
                query.setParameter("tempHref", tempHref);
                System.out.println(query.getSingleResult());
                count++;
            } catch (NoResultException e) {
                car deleteCar = session.get(car.class, id);
                deleteCar.setTradeAccount(null);
                deleteCar.setProductionEar(null);
                deleteCar.setPrice(null);                           // в последствии можно инкапсулировать в отдельный метод
                deleteCar.setModel(null);
                deleteCar.setPrice(null);
                deleteCar.setMileage(null);
                deleteCar.setEngineCapacity(null);
                deleteCar.setHref(null);
                deleteCar.setBrand(null);
                session.update(deleteCar);
                idNullList.add(id);
                session.getTransaction().commit();
                session.close();
                logger.info("entity nullified");
                e.printStackTrace();
                count++;
                continue;
            } catch (NullPointerException w) {
                logger.info("END list");

                break;
            } finally {
                session.close();
            }
        }


/**
 * Тут проверяется появились ли в сервисе новыые машины , если появились то создается вебдрайвер и запускается сборщик данных о машине,
 * он проходит по коллекции в которую добавились новые машины собирает о них данные  , создает сущности и
 * перезаписывает null строки в таблице если таковые имеются новыми сущностями иначе создает новую сущность в таблице.
 */

        while (true) {


            if (temp.size() != 0) {             // коллекция temp хранит в себе новые ссылки на авто , которых раньше не было , если она не пуста запускается сборщик
                createWebDriver();
                car = collector(temp.get(0));
                temp.remove(0);
                if (idNullList.size() != 0) {                                   // коллекция idNullList хранит в себе id сущностей которых объявили null, если она не пуста новые сущности машины вписываются в эти строки
                    session = factory.getCurrentSession();
                    session.beginTransaction();
                    String localId = idNullList.get(0);
                    car tempCar = session.get(car.class, localId);
                    tempCar.setBrand(car.getBrand());
                    tempCar.setEngineCapacity(car.getEngineCapacity());
                    tempCar.setHref(car.getHref());
                    tempCar.setMileage(car.getMileage());
                    tempCar.setModel(car.getModel());
                    tempCar.setPrice(car.getPrice());
                    tempCar.setProductionEar(car.getProductionEar());
                    tempCar.setTradeAccount(car.getTradeAccount());
                    session.update(tempCar);
                    idNullList.remove(0);
                    session.close();


                } else {
                    // в случае если нет строк которых объявили null создаются новые строки сущности
                    session = factory.getCurrentSession();
                    session.beginTransaction();
                    car newIdCar = new car(
                            car.getHref(),
                            car.getProductionEar(),
                            car.getPrice(),
                            car.getBrand(),
                            car.getModel(),
                            car.getEngineCapacity(),
                            car.getMileage(),
                            car.getTradeAccount());
                    session.persist(newIdCar);
                    session.getTransaction().commit();
                    session.close();


                }

            } else {
                break;

            }
        }
        logger.info("no new entities found");

        session.close();
        factory.close();


    }

    public static void main(String[] args) {



        Crawler crawler = new Crawler();
        // crawler.scanning();   // соберет все ссылки авто в продаже в БД
         crawler.createObjectsCarInTableDataBase(); // считывает объекты ссылок и на их основе создает сущности авто в БД (используется при первом запуске)


        /**
         * Когда нужно обновить БД ( удалить авто которых уже нет в продаже, добавить новые) нужно очистить таблицу в объектами ссылок и повторно запустить scanning(), потом использовать этот метод
         * метод удалит авто которых нет в продаже и дополнит таблицу новыми авто
         */
        //crawler.removingInvalidAndAddNewLinksFromDataBase();


    }
}
