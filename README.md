# User_Es_Project
User Elasticsearch Project

# CURL for validating a user
This API basically fetches the data from the provided jwt token, parses the input, and then saves the data to ES. More validations can be added here. 

curl --location --request POST 'localhost:8080/api/v1/user/validate'

# CURL for fetching a user profile's data
This API fetches a user profile's data directly from ES

curl --location --request GET 'localhost:8080/api/v1/user/?id=8akkeZEBzt6UelZk3o7o'

# CURL for manually saving a user's data directly to ES 
This API manually saves a user's data to ES (although idealy it should be saved from the validate API itself). Further validations can be added here. 

curl --location --request POST 'localhost:8080/api/v1/user/' \
--header 'Authorization: Basic ZWxhc3RpYzo4RVdSLV9STGVmdUxXME1nTVFrPQ==' \
--header 'Content-Type: application/json' \
--data-raw '{
    "username": "vansh test",
    "name": "vansh test",
    "age": 23
}'
