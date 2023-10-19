# Gerber File Parser
GerberFileParser is a Java class that provides methods to read and parse a Gerber file into a format suitable for additional processing by a Java program. It does not generate plots per se, but creates a list of graphical objects based on Java's Shape geometry along with meta-data that can be used to create plots and/or extract various information from the Gerber file.

Gerber File Parser adheres to Ucamco's *Gerber Layer Format Specification* version 2023.08. The complete specification is available on [Ucamco's web page](https://www.ucamco.com/en/gerber).
 
## History
In early 2022 I became aware that Gerber files were no longer just a graphical description of a PCB's layers but could also contain information related to the PCB's design such as component placement data needed by pick-and-place machines to assemble the board (for more details see [GerberX3](https://www.ucamco.com/en/gerber/gerber-x3)). A far as I was aware, this was the first published standard for conveying such information - most CAD vendors provide a means to export this data but it is usually in their own proprietary format. Since I am a contributor towards [OpenPnP](https://github.com/openpnp/openpnp), an open source software for running pick-and-place machines, I became interested in adding support to OpenPnP to read Gerber files in order to extract the placement data - hence, the development of Gerber File Parser. I initially began with a fork of Wayne Holder's [GerberPlot](https://github.com/wholder/GerberPlot). But, as my changes became more extensive and because I was really aiming to develop a stand alone library that could be utilized by other applications, it made sense to break this out as a separate project with its own repository.
 
## Requirements
Java 8 JDK, or later must be installed in order to compile the code.

## Limitations
The following deprecated Gerber command is not supported, any usage results in an exception:

[G91 - Set the Coordinate format to Incremental notation](https://www.ucamco.com/files/downloads/file_en/456/gerber-layer-format-specification-revision-2022-02_en.pdf?ac97011bf6bce9aaf0b1aac43d84b05f#page=173)


The following deprecated Gerber commands are are only supported if they confirm the default state, any other usage results in an exception:

[IP - Image Polarity](https://www.ucamco.com/files/downloads/file_en/456/gerber-layer-format-specification-revision-2022-02_en.pdf?ac97011bf6bce9aaf0b1aac43d84b05f#page=177)

[AS - Axis Select](https://www.ucamco.com/files/downloads/file_en/456/gerber-layer-format-specification-revision-2022-02_en.pdf?ac97011bf6bce9aaf0b1aac43d84b05f#page=175)

[IR - Image Rotation](https://www.ucamco.com/files/downloads/file_en/456/gerber-layer-format-specification-revision-2022-02_en.pdf?ac97011bf6bce9aaf0b1aac43d84b05f#page=178)

[MI - Mirror Image](https://www.ucamco.com/files/downloads/file_en/456/gerber-layer-format-specification-revision-2022-02_en.pdf?ac97011bf6bce9aaf0b1aac43d84b05f#page=180)

[OF - Offset](https://www.ucamco.com/files/downloads/file_en/456/gerber-layer-format-specification-revision-2022-02_en.pdf?ac97011bf6bce9aaf0b1aac43d84b05f#page=181)

[SF - Scale factor](https://www.ucamco.com/files/downloads/file_en/456/gerber-layer-format-specification-revision-2022-02_en.pdf?ac97011bf6bce9aaf0b1aac43d84b05f#page=182)


The following deprecated Gerber command operations are not supported, any usage results in an exception:

[Trailing Zero Omission](https://www.ucamco.com/files/downloads/file_en/456/gerber-layer-format-specification-revision-2022-02_en.pdf?ac97011bf6bce9aaf0b1aac43d84b05f#page=187)

[Incremental Notation](https://www.ucamco.com/files/downloads/file_en/456/gerber-layer-format-specification-revision-2022-02_en.pdf?ac97011bf6bce9aaf0b1aac43d84b05f#page=187)

[Rectangular Hole in Standard Apertures](https://www.ucamco.com/files/downloads/file_en/456/gerber-layer-format-specification-revision-2022-02_en.pdf?ac97011bf6bce9aaf0b1aac43d84b05f#page=188)

[Draws and Arcs with Rectangular Apertures](https://www.ucamco.com/files/downloads/file_en/456/gerber-layer-format-specification-revision-2022-02_en.pdf?ac97011bf6bce9aaf0b1aac43d84b05f#page=189) (Note, straight line draws with rectangular apertures are supported)

## Gerber Layer Format Specification Ambiguities
This is rather esoteric and probably would impact very few, if any, users of Gerber files but it is worth mentioning. The *Gerber Layer Format Specification* as currently written (version 2023.08) is rather vague with respect to how Aperture Attributes and Block Apertures work together. For instance, the AB (Aperture Block) command creates an aperture and adds it to the Aperture Dictionary. However it is not clear exactly *when* Aperture Attributes get attached to the created aperture (as the contents of the Attribute Dictionary can change during the block). Currently, Gerber File Parser attaches all Aperture Attributes that are in the Attribute Dictionary *at the time the Aperture Block is first opened*, i.e., when the %ABD...*% command is encountered. Any changes to the Attribute Dictionary that occur within the block are not reflected in the Aperture Attributes attached to the block. Furthermore, whenever the block aperture is flashed, its attached aperture attributes are attached to all graphical objects created by the flash. 

Additionally, since the SR (Step and Repeat) command effectively creates a Block Aperture (although it doesn't get added to the Aperture Dictionary), Gerber File Parser attaches all Aperture Attributes in the Attribute Dictionary at the opening of the SR block to the block. Which in turn means those Aperture Attributes get attached to all objects created by each repeat (flash) of the block.

In my view, these are consistent with how Aperture Attributes work with the standard and macro apertures as well as regions. Of course, this is all subject to change should it be clarified in future revisions of the *Gerber Layer Format Specification*.
