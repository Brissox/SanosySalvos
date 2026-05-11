import json
import logging

import aio_pika

from config import settings

logger = logging.getLogger(__name__)


async def iniciar_consumer_cerrado(on_reporte_cerrado):
    connection = await aio_pika.connect_robust(settings.rabbitmq_url)
    channel = await connection.channel()
    await channel.set_qos(prefetch_count=10)

    queue = await channel.declare_queue(settings.queue_reporte_cerrado, durable=True)

    logger.info("Consumer escuchando cola '%s'", settings.queue_reporte_cerrado)

    async with queue.iterator() as msgs:
        async for message in msgs:
            async with message.process(requeue=True):
                try:
                    data = json.loads(message.body)
                    reporte_id = data.get("reporteId")
                    if reporte_id:
                        await on_reporte_cerrado(int(reporte_id))
                except Exception:
                    logger.exception("Error procesando reporte cerrado")
