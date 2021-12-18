import xml.etree.ElementTree as ET


class XMLParser:
    def __init__(self, data: bytes):
        self.tree = ET.ElementTree(ET.fromstring(data.decode("utf-8")))
        self.mode = self.tree.getroot().attrib["mode"]
