Feature: Registry api tests
  Background:
    * url baseurl
    * string mobile = "0000000010"
    * def password = "opensaber@123"
    * def student = {"contactDetails": {"email": "peter@sahaj.ai","mobile": "#(mobile)"}}
    * string registryUrl = "http://localhost:8081"
    * string authUrl = "http://localhost:9990"

  Scenario: health check
    Given path 'health'
    When method get
    Then status 200

  Scenario: Should able to update the nested entity without osid
    * print mobile, password
    Given path "api/v1/Student/invite"
    And request student
    When method post
    Then status 200
    And match response.params.status == "SUCCESSFUL"

    * def studentOsid = response.result.Student.osid
    * def sleep = function(millis){ java.lang.Thread.sleep(millis) }
    * sleep(1000)

   * string formRequest = "grant_type=password&client_id=registry-frontend&username=" + mobile + "&password="+password
    Given url authUrl
    Given path "auth/realms/ndear/protocol/openid-connect/token"
    And header Content-Type = "application/x-www-form-urlencoded"
    And request formRequest

    When method post
    Then status 200

    * string accessToken = 'Bearer ' + response.access_token
    * print 'the value of accessToken is:', accessToken

    Given url registryUrl
    Given path "api/v1/Student"
    And header Authorization = accessToken
    When method get
    Then status 200

    * string contactOsid = response[0].contactDetails.osid
    * def updateRequestBody = read('updateRequestBody.json')

    Given url registryUrl
    Given path "api/v1/Student/" + studentOsid
    And header Authorization = accessToken
    And request updateRequestBody
    When method put
    Then status 200
    And match response.params.status == "SUCCESSFUL"

    Given url registryUrl
    Given path "api/v1/Student"
    And header Authorization = accessToken
    When method get
    Then status 200
    And match response[0].identityDetails.fullName == "Peter Parker"
    And match response[0].identityDetails.identityHolder.value == "456456"
    And match response[0].guardianDetails.fullName == "Tony Stark"
    * print response
