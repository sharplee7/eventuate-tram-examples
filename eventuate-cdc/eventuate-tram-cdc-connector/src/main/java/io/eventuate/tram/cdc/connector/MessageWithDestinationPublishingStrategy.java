package io.eventuate.tram.cdc.connector;

import io.eventuate.local.common.PublishingStrategy;

import java.util.Optional;

public class MessageWithDestinationPublishingStrategy implements PublishingStrategy<MessageWithDestination> {

  @Override
  public String partitionKeyFor(MessageWithDestination messageWithDestination) {
    return messageWithDestination.getPartitionId().orElseGet(messageWithDestination::getId);
  }

  @Override
  public String topicFor(MessageWithDestination messageWithDestination) {
    return messageWithDestination.getDestination();
  }

  @Override
  public String toJson(MessageWithDestination messageWithDestination) {
    return messageWithDestination.toJson();
  }

  @Override
  public Optional<Long> getCreateTime(MessageWithDestination messageWithDestination) {
    return Optional.empty(); // TODO
  }
}
