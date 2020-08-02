# elastic-ads
Simple service exposing API to query ad stats

## api

API can be browsed using [swagger](https://elastic-ads.herokuapp.com/swagger-ui.html) which is available when application is deployed. The link points to an instance deployed in heroku free-dyno so start-up can take some time.

Filters for `dimensions` are also accepted as single values. Swagger schema shows a map of lists.

Data returned from the API always have the same flat structure. All fields selected for the aggregations are returned as simple json objects.

## stack

The application is composed of a spring boot app running a spring batch job and uses spring data elasticsearch client for indexing and querying data.