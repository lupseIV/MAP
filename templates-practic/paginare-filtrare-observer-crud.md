### Workflow



-paginare + filtrare

-observer

-BaseService si EntityDbRepo-paginat(saveToDatabase method trebuie sa salveze cu tot cu id)

-CRUD ui full

-Table View paginat si nepaginat



#### Backend

1. creare entitati in domain entities

2\. foloseste SqlSchemaGenerator pentru a genera /db/schema.sql (sa fie facuta conexiunea la baza de date si puse datele in DatabaseConnection)

3\. adaugare date tabel cu insert

4\. creare validatoare pt fiecare clasa(eventual goale si logica ma incolo)

5\. creare implementari in repository.database in folder implementations

6\. creare implemntari in service in folder implementations



#### Frontend

-creare filtre

1. creare instante de service in metoda init() din GraphicUserInterface
2. deschide o fereastra cu functia OpenWindow
3. adaugare butoane pentru panouri diferite in functia configureController folosing functia addMenuOptions de la controllerul Main ( aici se seteaza si serviuri pentru Controllerele deschise de butoane prin interfata functionala ControllerConfigurator
4. Creare de Controller in gui.implementations doar aici trebe adaugat in teorie

&nbsp;	1. extinde AbstractPagingTableView pentru tableview cu paginare cu observer

&nbsp;	2. extinde AbstractTableView pentru table fara paginare observer

&nbsp;	3. Pentru  operatii crud implementeaza AddDialog, DeleteDialog, UpdateDialog si trebuie dupa implementate metodele abstracte din interfete si epalta intr-o functie handleAdd, handleDelete, handleUpdate metoda execAdd,

execDelete, execUpdate

&nbsp;	4. DynamicFormDialog creeaza un dialog cu titlu dat si lista de form fields (FormField are metode statice care creeaza fielduri)

