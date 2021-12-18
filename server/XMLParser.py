import xml.etree.ElementTree as ET


class XMLParser:
    def __init__(self, data: str):
        self.tree = ET.ElementTree(ET.fromstring(data))
        self.mode = self.tree.getroot().attrib["mode"]
