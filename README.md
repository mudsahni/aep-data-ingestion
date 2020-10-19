# Adobe Experience Platform (AEP) Data Ingestion API Connection

### Run Locally
add yaml file to resources folder.

build code

`mvn package`

run

`java -jar target/ep-data-ingestion-1.0-FULL-shaded.jar -datasetId=<datasetId> -dataFormat=<json/parquet> -filePath=<file-location> -fileName=<filename-for-aep>`

