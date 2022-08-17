# Mock WS
The mock WS is a Web Application to mock response for local tests porpoise.
 
### How to use
There is a `mockdefinitions.json` file in `resources` folder, that contains a list of mocks definition, following de next structure:
```json
[
  {
    "url": "/url-request",
    "file": "response.json",
    "verbs": ["GET", "POST"], 
    "param": { "name":  "value"},
    "status": 200,
    "ratioError": 10,
    "delay": 2000
  }
]
```
The fields are: 
- url: Is the url request to mock server response.
- file: Is the file that content the json body response.
- verbs: Is a list with the accepted verbs for this request/response
- status: Is the status response
- ratioError: Is a percentual possibility to return an error. Default is 0.
- delay: If we want apply a delay for a response, we can specify a number in milliseconds for the response.
