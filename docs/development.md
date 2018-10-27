# Development  

## Prerequisites  

- [aws cli](https://docs.aws.amazon.com/cli/latest/userguide/installing.html)  
- [aws sam cli](https://github.com/awslabs/aws-sam-cli/blob/develop/docs/installation.rst)  
- Maven
- Node
- `npm install`

## Building the Code  

```bash
mvn clean install
```

## Running the Tests  

```bash
mvn clean test
```

## Running the Integration Tests  

```bash
npm run int-tests-db && mvn clean integration-test
```

## Dependency vulnerability scan  

```bash
npm run osa
```

## Run it locally  

```bash
npm start
```

### Verify everything is running locally  

```bash
lsof -Pn -i4 | grep -E ':8000|:3000'
```

## Local Debugging with IntelliJ  

1- Create a [Remote JVM Configuration](https://www.jetbrains.com/help/idea/run-debug-configuration-remote-debug.html) for port `localhost:5858`. Make sure to load the `hb-api` module classpath.  
2- Place a breakpoint in the desired location  
3- Run `npm run debug`  
4- Invoke the lambda  
5- Click the Debug button next to the newly created configuration  
