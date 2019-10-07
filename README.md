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
    "status": 200
  }
]
```
The fields are: 
- url: Is the url request to mock server response.
- file: Is the file that content the json body response.
- verbs: Is a list with the accepted verbs for this request/response
- status: Is the status response
