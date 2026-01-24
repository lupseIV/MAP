### Workflow



* database pentru operatii asincron - Hikari sau Autocloseable

&nbsp;	Hikari - exact la fel operatiile ca normal

&nbsp;		- trebe inchis pool la final

&nbsp;		- trebe adaugata dependenta gradle

&nbsp;	Autoclosable -  trebe facut conn.get() pentru ca conexiunea e intr-un autoclosable wrapper

* completable future pe toate operatiile din service
* trebuie implementate aceleasi lucruri ca la paginare-filtra

## Backend

1\. creare entitati in domain entities



2\. foloseste SqlSchemaGenerator pentru a genera /db/schema.sql (sa fie facuta conexiunea la baza de date si puse datele in DatabaseConnection)

3\. adaugare date tabel cu insert

4\. creare validatoare pt fiecare clasa(eventual goale si logica ma incolo)

5\. creare implementari in repository.database in folder implementations

6\. creare implemntari in service in folder implementations



## Frontend

-creare filtre

1\. creare instante de service in metoda init() din GraphicUserInterface

2\. deschide o fereastra cu functia OpenWindow

3\. adaugare butoane pentru panouri diferite in functia configureController folosing functia addMenuOptions de la controllerul Main ( aici se seteaza si serviuri pentru Controllerele deschise de butoane prin interfata functionala ControllerConfigurator

4\. Creare de Controller in gui.implementations doar aici trebe adaugat in teorie

