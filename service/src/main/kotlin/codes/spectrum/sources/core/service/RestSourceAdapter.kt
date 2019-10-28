package codes.spectrum.sources.core.service

import codes.spectrum.sources.ISourceHandler
import codes.spectrum.sources.SourceContext
import codes.spectrum.sources.core.rest.RestSourceAdapter as RestSourceAdapterNew

@Deprecated("Используй codes.spectrum.sources.core.rest.RestSourceAdapter")
abstract class RestSourceAdapter<Q,R>(
    internalSource : ISourceHandler<out SourceContext<Q,R>>,
    contextClass: Class<out SourceContext<Q,R>>
) : RestSourceAdapterNew<Q, R>(
    internalSource,
    contextClass
)

