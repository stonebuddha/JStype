JStype
======
JStype is a static type analyser for JavaScript with tunable sensitivity. It is described under abstract interpretation, and it defines abstract semantics over a CESK-style concrete semantics. JStype is implemented in Java, using Nashorn in JRE to parse JavaScript. JStype can do analysis using different setting of sensitivity, for example, 'fs' stands for flow-sensitive context-insensitive analysis and 'stack-5-4' stands for 5-CFA call site sensitivity and 4-CFA heap sensitivity.
