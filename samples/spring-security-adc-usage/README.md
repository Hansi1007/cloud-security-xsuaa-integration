# Description - UNDER CONSTRUCTION!!!
This sample uses the SAP application router as a web server and forwards requests to a Java Spring back-end application running on Cloud Foundry.
In a typcal UI5 application, the application router serves HTML files and REST data would be provided by a back-end application. To focus on the security part, UI5 has been omitted.

# Coding
This sample is using the spring-security project. As of version 5 of spring-security, this includes the OAuth resource-server functionality. The security configuration needs to configure JWT for authentication.
Please see the [`spring-xsuaa` descriptions](../spring-xsuaa/README.md) for details.

# Deployment To Cloud Foundry or SAP HANA XS Advanced
To deploy the application, the following steps are required:
- Configure the Application Router
- Compile the Java application
- Create an XSUAA service instance
- Configure manifest.yml
- Deploy the application
- Assign Role to your user
- Access the application

## Configure the Application Router

The [Application Router](./approuter/package.json) is used to provide a single entry point to a business application that consists of several different apps (microservices). It dispatches requests to backend microservices and acts as a reverse proxy. The rules that determine which request should be forwarded to which _destinations_ are called _routes_. The application router can be configured to authenticate the users and propagate the user information. Finally, the application router can serve static content.

## Compile the Java Application
Run maven to package the application
```shell
mvn clean package -DskipTests
```
> Note: As of now the JUnit tests may not run unless you've launched OPA as part of the docker container.
> In case the ADC service does not run at `http://localhost:8181` you need to overwrite the `ADC_URL`
> as part of system environment variable or via the [application-uaamock.properties](src/main/resources/application-uaamock.properties). 

## Create the XSUAA Service Instance
Use the [xs-security.json](./xs-security.json) to define the authentication settings and create a service instance
```shell
cf create-service xsuaa application xsuaa-adc -c xs-security.json
```

## Configure the manifest
The [vars](../vars.yml) contains hosts and paths that you might need to adopt.

## Deploy the application
Deploy the application using cf push. It will expect 1 GB of free memory quota.

```shell
cf push --vars-file ../vars.yml
```

## Access Authorization Decision Controller (ADC) via Open Policy Agent endpoints
* `<ADC_URL>/v1/policies`
* `<ADC_URL>/v1/data`
* `<ADC_URL>/v1/data/rbac/allow` POST request with Content-Type: application/json and payload:
```
{
	"input": {
		"user": "",
		"actions": {
            ...write
        }
	}
}
```
should return true, whereas this payload for the same `user` but different `action`:
```
{
	"input": {
		"user": "",
		"actions": {
            ...write
        }
	}
}
```
should return false, as the user has no policy assigned with rule (action) `write`. 

Apply also a check on scope or attribute values using a token that simulates an Admin user:
```
{
	"input": {
		"user": ""
	}
}
```

Find the current API documentation of OPA (Open Policy Agent) [here](https://www.openpolicyagent.org/docs/latest/rest-api/).

## [OPTIONAL] Configure the local environment
You need to configure the Url of the Authorization Decision Controller (`ADC_URL`) as part of system environment variable or via the [application-uaamock.properties](src/main/resources/application-uaamock.properties). 
You can enter the url of the ADC service that runs locally, e.g. in a docker container.

## [OPTIONAL] Test locally
```
docker-compose up -d
mvn spring-boot:run -Dspring-boot.run.profiles=cloud,uaamock
```
    
When your application is successfully started (pls check the console logs) use a Rest client such as `Postman Chrome Extension`. Then you can perform a GET request to `http://localhost:8080/v1/method` and set an `Authorization` header with the value 
```
Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImprdSI6Imh0dHA6Ly9sb2NhbGhvc3Q6MzMxOTUvdG9rZW5fa2V5cyIsImtpZCI6ImxlZ2FjeS10b2tlbi1rZXkifQ.eyJleHRfYXR0ciI6eyJ6ZG4iOiIifSwiemlkIjoidWFhIiwiemRuIjoiIiwiZ3JhbnRfdHlwZSI6InVybjppZXRmOnBhcmFtczpvYXV0aDpncmFudC10eXBlOnNhbWwyLWJlYXJlciIsInVzZXJfbmFtZSI6InZpZXdlciIsIm9yaWdpbiI6InVzZXJJZHAiLCJ4cy5zeXN0ZW0uYXR0cmlidXRlcyI6eyJ4cy5yb2xlY29sbGVjdGlvbnMiOlsiVmlld2VyIl19LCJleHAiOjY5NzQwMzE2MDAsImlhdCI6MTU2NDA2MjI3OSwiZW1haWwiOiJ2aWV3ZXJAdGVzdC5vcmciLCJjaWQiOiJzYi1zcHJpbmctc2VjdXJpdHktYWRjLXVzYWdlIXQxNDg2NiJ9.Xzx1pEWFpVyR8pAn_7RCwJ02bb6iH1HwYSJKgSV3npteeP_qs_8VLHNWDqd9xOagMb0VDpgiDtAcA-lCETElEizD4vNSQuPVRHnSfZxiHuEhonik1BQ2WElQOZ-R0N5RJnaOlpBtNehOiqzkJCWL4STOYGakmMcncwlBCO378dNTa0aIdKD6ftFT3Aq5Vv4ll33cK9N4UmbgHuiyfmVKVI73OxEeLbnKnucOkdj-up2jOk7IVpwlOThGuQFxXcOmdtM9gmmTcr0Popu3-XV6GpAJrIHz4j02QCyMTckQL57VTHxUfp2iTOoUUD0I9On-srNo4hdmpcU_h4lhZv890A
```

Alternatively you can also debug the [TestControllerTest](src/test/java/sample.spring.adc/TestControllerTest.java) JUnit Test.  

## Cockpit administration tasks: Assign Policy to your User
When accessing your application endpoints on Cloud Foundry via the Application Router, you get redirected to a login-screen to authenticate yourself. 
But your application will respond with error status code `403` (`unauthorized`) in case you do not have any Policies assigned. 

!!!TODO!!!! 
That's why you need to assign as part of your Identity Provider, e.g. SAP ID Service the deployed Role Collection(s) such as `Viewer` or `Administrator` to your user as depicted in the screenshot below and as documented [here](https://help.sap.com/viewer/65de2977205c403bbc107264b8eccf4b/Cloud/en-US/9e1bf57130ef466e8017eab298b40e5e.html).

![](../images/SAP_CP_Cockpit_AssignRoleCollectionToUser.png)

Further up-to-date information you can get on sap.help.com:
- [Maintain Role Collections](https://help.sap.com/viewer/65de2977205c403bbc107264b8eccf4b/Cloud/en-US/d5f1612d8230448bb6c02a7d9c8ac0d1.html)
- [Maintain Roles for Applications](https://help.sap.com/viewer/65de2977205c403bbc107264b8eccf4b/Cloud/en-US/7596a0bdab4649ac8a6f6721dc72db19.html).

## Access the application
After deployment, the Application Router will trigger authentication automatically when you access one of the following URLs:

* `https://spring-security-adc-usage-web-<ID>.<LANDSCAPE_APPS_DOMAIN>/v1/method` - GET request to execute a method secured with Spring Global Method Security. This method requires a policy to grant action = `read`.

> Note: https://spring-security-adc-usage-web-<ID>.<LANDSCAPE_APPS_DOMAIN> points to the url of the Application Router. Get all app routes with `cf apps`.


## Clean-Up
```
cf delete approuter-spring-security-adc-usage
cf delete cf delete spring-security-adc-usage
cf delete-service xsuaa-adc
```