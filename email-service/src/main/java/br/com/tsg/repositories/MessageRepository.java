package br.com.tsg.repositories;

import br.com.tsg.collections.Message;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface MessageRepository extends MongoRepository<Message, ObjectId> {
}
