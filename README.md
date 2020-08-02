# elastic-ads
Simple service exposing API to query ad stats

## api

API can be browsed using [swagger](https://elastic-ads.herokuapp.com/swagger-ui.html) which is available when application is deployed. The link points to an instance deployed in heroku free-dyno so start-up can take some time.

Filters for `dimensions` are also accepted as single values. Swagger schema shows a map of lists.

Data returned from the API always have the same flat structure. All fields selected for the aggregations are returned as simple json objects.

Example:
```shell script
curl -X POST "https://elastic-ads.herokuapp.com/v1/ad-stats" -H  "Content-Type: application/json" \
  -d '{"aggregations":{"dimensions":["DATASOURCE"],"time":"DAY"},"filters":{"range":{"from":"2020-01-02","to":"2020-08-02"}},"metrics":["CLICKS","CTR"]}"
```

will return:
```json
[
  {
    "date": "2020-01-02T00:00:00.000Z",
    "datasource": "Twitter Ads",
    "totalClicks": 278,
    "ctr": 9.138724523339908
  },
  {
    "date": "2020-01-02T00:00:00.000Z",
    "datasource": "Google Ads",
    "totalClicks": 52,
    "ctr": 0.18229623137598597
  },
  {
    "date": "2020-01-03T00:00:00.000Z",
    "datasource": "Twitter Ads",
    "totalClicks": 234,
    "ctr": 14.382298709280885
  },
  ...
  {
    "date": "2020-02-14T00:00:00.000Z",
    "datasource": "Twitter Ads",
    "totalClicks": 47,
    "ctr": 10.398230088495575
  },
  {
    "date": "2020-02-14T00:00:00.000Z",
    "datasource": "Google Ads",
    "totalClicks": 60,
    "ctr": 0.2576655501159495
  }
]
```

## stack

The application is composed of a spring boot app running a spring batch job and uses spring data elasticsearch client for indexing and querying data.