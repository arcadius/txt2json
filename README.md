# txt2json is just a demo project to showcase spring-boot capabilities

* Build with ` ./gradlew clean build `
* The project uses the H2 database
* To turn off file validation, please add header `enableFileValidation` with value `false` to the request
* Only `text/plain` files are supported
* To test the app in a web browser, please start server and go to http://localhost:8080/
* The app can also be tested using curl on the command line in the project root directory:
  ``curl http://localhost:8080/api/txt2json -F file=@src/test/resources/EntryFile.txt -OJ
  ``
* Jacoco test coverage is under `build/reports/jacoco/testCodeCoverageReport/html/index.html`
* NOTE: The GeoIP filtering and access logging do not really belong to this API. Ideally, they should be handled by an API Gateway. Its being implemented here to showcase the power of Spring `Filters` and `Interceptors`
* TODO: add Swagger documentation
* TODO: add caching to the GeoIp calls
* TODO: Good practice dictates we have at least the `uuid` or the `id` in the `Outcome.json` output file just in case we have duplicates