# Resolver
Pwidresolver converts archive URLs to [PWIDs](https://www.iana.org/assignments/urn-formal/pwid) and PWIDs back to URLs that resolve the archived website

adapted from https://github.com/netarchivesuite/NAS-research/tree/master/src/main/java/dk/netarkivet/research/pwid

# Build
docker compose up

# Test Endpoints
```
curl -v http://localhost:8080/heartbeat
```
```
curl -v "http://localhost:8080/pwid?archiveString=urn:pwid:webarchiv.onb.ac.at:2013-12-03T17:03:03Z:page:http://m.onb.ac.at/prunksaal.htm"
```
```
curl -v "http://localhost:8080/pwid?archiveString=https://webarchiv.onb.ac.at/web/20131203170303/http://m.onb.ac.at/prunksaal.htm"
```
```
curl -v "http://localhost:8080/pwid?archiveString=urn:pwid:archive.org:2022-11-27T18:33:21Z:page:https://www.iana.org/assignments/urn-formal/pwid"
```
```
curl -v "http://localhost:8080/pwid?archiveString=https://web.archive.org/web/20221127183321/https://www.iana.org/assignments/urn-formal/pwid"
```
