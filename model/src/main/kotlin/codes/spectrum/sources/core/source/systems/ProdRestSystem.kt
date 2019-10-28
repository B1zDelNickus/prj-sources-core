package codes.spectrum.sources.core.source.systems

import codes.spectrum.sources.core.SourceDefinition
import codes.spectrum.sources.core.source.RestSystem

object ProdRestSystem : RestSystem(
    name = "Прод",
    url = SourceDefinition.Instance.prodHostName,
    index = 2
)