# MiroTestTask

To run type: mvnw spring-boot:run

Classes in src\main\java\com\example\demo\widgetstorages are different implementations of in-memory storages:
* WidgetStorage — the simpliest one, supports only basic operations according to the task;
* WidgetStorageWithPaging — supports pagination (Complication 1);
* WidgetStorageWithFiltering — supports filtering (Complication 2);
* WidgetStorageWithPagingAndFiltering — supports both pagination and filtering.
