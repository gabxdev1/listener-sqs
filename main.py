def send_partition_to_sqs(iterator):
    import boto3
    import json
    import uuid

    sqs = boto3.client("sqs", region_name="SQS_REGION")
    queue_url = "SQS_QUEUE_URL"

    buffer = []
    batch_size = 10

    for row in iterator:

        correlation_id = str(uuid.uuid4())

        body = {
            "message_uuid": correlation_id,
            "team_id": row["TEAM_ID"],
            "name": row["NAME"],
            "country": row["COUNTRY"],
            "classification_via": row["CLASSIFICATION_VIA"],
            "export_date": str(row["DATE"])
        }

        entry = {
            "Id": correlation_id[:80],
            "MessageBody": json.dumps(body),
            "MessageAttributes": {
                "correlation_id": {
                    "DataType": "String",
                    "StringValue": correlation_id
                }
            }
        }

        # Se fila for FIFO, inclua isso:
        # entry["MessageGroupId"] = "glue-export-times"
        # entry["MessageDeduplicationId"] = correlation_id

        buffer.append(entry)

        if len(buffer) == batch_size:
            sqs.send_message_batch(
                QueueUrl=queue_url,
                Entries=buffer
            )
            buffer = []

    if buffer:
        sqs.send_message_batch(
            QueueUrl=queue_url,
            Entries=buffer
        )
