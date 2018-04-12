# Information-Retrieval-Models

- Boolean Model : is an IR Model for retrieving the matched documents that satisfies the entered Query, This Model takes the Documents, Create a Boolean Model and process the entered query(can handle complex queries).This Boolean Model is so simple as it was built as a 2D Matrix of 0's and 1's(True and False) which by can get the matched documents.
- Inverted Index Model : is an IR Model for retrieving the matched documents that satisfies the entered Query, This Model takes the Documents, Create a Boolean Model and process the entered query(can handle complex queries). This Model is Built by using the positing lists for each token, We can define the matched documents by complementing(NOT), intersecting(AND) and merging(OR) these lists.

Note:
- These Models can't handle entering a new token in the query that never been in any of the documents.
- The Query processing may be not 100% working in more complex queries.
