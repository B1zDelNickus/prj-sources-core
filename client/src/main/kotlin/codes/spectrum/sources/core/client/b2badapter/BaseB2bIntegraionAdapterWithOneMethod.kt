package codes.spectrum.sources.core.client.b2badapter

import codes.spectrum.data.SourceDescriptor
import codes.spectrum.serialization.json.Json
import codes.spectrum.sources.ISourceHandler
import codes.spectrum.sources.SourceContext
import org.slf4j.Logger

abstract class BaseB2bIntegraionAdapterWithOneMethod<Q, R>(
        client: ISourceHandler<SourceContext<Q, R>>,
        logger : Logger,
        descriptor: SourceDescriptor
): BaseB2bIntegrationAdapter<Q, R>(client, logger, descriptor){

    override fun resultFromData(data: Any?) = if (data != null) try { Json.read(Json.stringify(data), resultFromDataClazz)} catch (thr: Throwable){null} else null

    abstract val resultFromDataClazz: Class<R>
}
