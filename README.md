# Crawler1
Сборщик 


Приложение использует библиотеку Selenium  для имитаций действий пользователя, для составления списка машин находящихся в продаже и Hibernate для взаимодействия с БД
, создания и изменения в ней сущностей ссылок на авто и самих авто.


Откройте проект в IDEA

Настройка
1. по адресу src/main/resources/hibernate.cfg.xml
установите имя пользователя , пароль , URL адрес 

        <property name="connection.url">jdbc:mysql://localhost:3306/my_db?useSSL=false&amp;serverTimezone=UTC</property>   URL адрес 
        <property name="connection.driver_class">com.mysql.cj.jdbc.Driver</property>
        <property name="connection.username">bestuser</property>       имя пользователя учетной записи в БД 
        <property name="connection.password">bestuser</property>        пароль  
        <property name="current_session_context_class">thread</property>
        <property name="dialect">org.hibernate.dialect.MySQLDialect</property>
        <property name="show_sql">true</property>
  




2. Создать в БД таблицы 
CREATE TABLE my_db.car (
  id int NOT NULL AUTO_INCREMENT,
  href varchar(300),
  production_ear varchar(100),
  price varchar(100),
  brand varchar(100),
  model varchar(100),
  engine_capacity varchar(100),
  mileage varchar(100),
  trade_account varchar(100),
  PRIMARY KEY (id)
);

CREATE TABLE my_db.href (
  id int NOT NULL AUTO_INCREMENT,
  href varchar(300),
  PRIMARY KEY (id)
);

3. Для первого запуска используется метод scanning(), он соберет в БД все ссылки на авто (
     Этот метод заходит на стартовую страницу сервиса,
      показывает все марки автомобилей, создает список всех автомобилей,
      затем метод проходит по списку, переходя к
      вкладке каждой марки автомобиля и добавлет туда все ссылки на  автомобили
      в базу данных.)
     
     Далее используем createObjectsCarInTableDataBase()
     Этот метод запрашивает у БД объекты ссылки, проходит по ним собирает инфрмацию ,
     создает объект авто и помещает его в таблицу объектьов авто в БД
     
     Спустя время когда на сервисе какие то мащины были убраны , а какие то добавлены , нужно очистить табдицу href 
     truncate my_db.href;
     и вновь запустить scanning()
     метод соберет свежий список ссылок на авто
     
     После обновления сриска ссылок на авто надо использовать метод removingInvalidAndAddNewLinksFromDataBase()
     Этот метод обращается к обновленной таблице сущностей ссылок на авто. Метод выявляет новые ссылки и создает и добавляет на их основе новые сущности машины в БД.
      Метод выявляет устаревшие ссылки которых больше нет на сервисе и удаляет сущности авто с этими ссылками.
      
      Таким образом Вы получаете таблицу в БД с обьявлениями о продаже авто которые содержат в себе информацию
      ссылку на авто
      год выпуска или покупки, 
      цену,
      бренд,
      модель,
      объем двигателя,
      пробег,
      имя аккаунта продавца .
  




