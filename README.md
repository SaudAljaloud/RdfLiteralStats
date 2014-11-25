**RdfLiteralStats**
-------------------

This is a tool for providing statistics about RDF data files in terms of literal usage.

Parsing RDF is done by the Jena arq (Apache License, Version 2.0).

**To compile the code:**

> mvn clean install  
> mvn dependency:copy-dependencies

give yourself a permission to run the script below, if needed. Then,

    sh run2.sh <rdf path>

The result will be printed to screen and to three files (termsMap.txt, Histogram.txt stats.txt).  
stats.txt contains the same form as the sample below.  
Histogram.txt contains the distribution of the word usage across the literal values within the RDF.  
termsMap.txt contains the words (while space tokened) included in the literal values.

**Licensing:**

This code is licensed under the MIT License attached in the parent directory.

**Output Sample:**

    Triples Count: 17686178
    Number of Literal objects with duplicates: 9129307
    Number of Literal objects NO duplicates: 2046362
    Average literals against triples: 0.51618314
    http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/productPropertyTextual6	 10043
    http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/productPropertyNumeric6	 10170
    http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/productPropertyNumeric4	 24939
    http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/productPropertyTextual4	 24998
    http://xmlns.com/foaf/0.1/mbox_sha1sum                      	 25592
    http://xmlns.com/foaf/0.1/name                              	 25592
    http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/productPropertyTextual5	 27430
    http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/productPropertyNumeric5	 27569
    http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/productPropertyTextual1	 50000
    http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/productPropertyTextual2	 50000
    http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/productPropertyTextual3	 50000
    http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/productPropertyNumeric3	 50000
    http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/productPropertyNumeric1	 50000
    http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/productPropertyNumeric2	 50000
    http://www.w3.org/2000/01/rdf-schema#label                  	 76085
    http://www.w3.org/2000/01/rdf-schema#comment                	 76085
    http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/rating3	 349500
    http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/rating1	 349625
    http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/rating2	 349912
    http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/rating4	 350090
    http://purl.org/dc/elements/1.1/title                       	 500000
    http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/reviewDate	 500000
    http://purl.org/stuff/rev#text                              	 500000
    http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/price	 1000000
    http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/deliveryDays	 1000000
    http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/validFrom	 1000000
    http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/validTo	 1000000
    http://purl.org/dc/elements/1.1/date                        	 1601677
    Plain Literals: 1203354
    Typed-Literals: 7925953
    The average length of literals: 89
    http://www.w3.org/2001/XMLSchema#string                     	 212471
    http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/USD	 1000000
    http://www.w3.org/2001/XMLSchema#date                       	 1601677
    http://www.w3.org/2001/XMLSchema#dateTime                   	 2500000
    http://www.w3.org/2001/XMLSchema#integer                    	 2611805
    Number of words with duplicates: 83003158
	Word count histogram:
    1              	 412372
    2              	 186427
    3              	 61793
    4              	 15380
    5              	 3100
    6              	 508
    7              	 65
    8              	 12
    9              	 2
    10             	 3238
    100            	 90880
    1000           	 406
    10000          	 219
    100000         	 10

