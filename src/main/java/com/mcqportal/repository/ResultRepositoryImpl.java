package com.mcqportal.repository;

import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.stereotype.Repository;

@Repository
public class ResultRepositoryImpl implements ResultRepositoryCustom {
    private final MongoTemplate mongoTemplate;

    public ResultRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public double averagePercentage() {
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.group().avg("percentage").as("avg")
        );
        AggregationResults<Document> results = mongoTemplate.aggregate(agg, "results", Document.class);
        Document doc = results.getUniqueMappedResult();
        if (doc != null && doc.get("avg") != null) {
            double avg = ((Number) doc.get("avg")).doubleValue();
            return Math.round(avg * 100.0) / 100.0;
        }
        return 0.0;
    }
}
