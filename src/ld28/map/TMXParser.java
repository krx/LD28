package ld28.map;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import jgame.Animation;
import jgame.util.FileIOHelper;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class TMXParser {
	private static final String MAP_DATA_ROOT = "/maps/data/";
	private static final String TILESET_ROOT = "/maps/tilesets/";
	
	public static Map loadTMXMap(String path) {
		InputStream mapData = FileIOHelper.loadResource(path);
		Tile[][] mapTiles;
		ArrayList<BufferedImage> tileImages = new ArrayList<BufferedImage>();
		tileImages.add(new BufferedImage(1,1, BufferedImage.TYPE_INT_ARGB));
		
		try {
			// Load document
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(mapData);
			doc.getDocumentElement().normalize();
			
			// Load initial map data
			NodeList mapNodes = doc.getElementsByTagName("map");
			Node map = mapNodes.item(0);
			Element eMap = null;
			if(map.getNodeType() == Node.ELEMENT_NODE)
				eMap = (Element) map;
			int w = Integer.parseInt(eMap.getAttribute("width"));
			int h = Integer.parseInt(eMap.getAttribute("height"));
			int tw = Integer.parseInt(eMap.getAttribute("tilewidth"));
			int th = Integer.parseInt(eMap.getAttribute("tileheight"));
			
			// Load TileSets and images
			NodeList tsNodes = doc.getElementsByTagName("tileset");
			for(int i = 0; i < tsNodes.getLength(); i++) {
				Node ts = tsNodes.item(i);
				NodeList children = ts.getChildNodes();
				Element eImg = null;
				for(int j = 0; j < children.getLength(); j++) {
					if(children.item(j).getNodeType() == Node.ELEMENT_NODE && children.item(j).getNodeName().equals("image")) {
						eImg = (Element) children.item(j);
						break;
					}
				}
				Element ets = null;
				if(ts.getNodeType() == Node.ELEMENT_NODE) {
					ets = (Element) ts;
				}
				//FIX SOURCE ROOT
				TileSet temp = new TileSet(ets.getAttribute("name"), TILESET_ROOT + eImg.getAttribute("source"), Integer.parseInt(ets.getAttribute("firstgid")), tw, th);
				tileImages.addAll(temp.getTiles().getAllSubImages());
			}
			// Set map size
			mapTiles = new Tile[h][w];
			// Fill map with correct tiles
			NodeList nlData = doc.getElementsByTagName("data");
			Node data = null;
			for(int j = 0; j < nlData.getLength(); j++) {
				if(nlData.item(j).getNodeType() == Node.ELEMENT_NODE) {
					data = nlData.item(j);
					break;
				}
			}
			NodeList tileNodes = data.getChildNodes();
			ArrayList<Element> eTiles = new ArrayList<Element>();
			for(int j = 0; j < tileNodes.getLength(); j++) {
				if(tileNodes.item(j).getNodeType() == Node.ELEMENT_NODE && tileNodes.item(j).getNodeName().equals("tile")) {
					eTiles.add((Element) tileNodes.item(j));
				}
			}
			// Checks the gid of every data element, which corresponds to the image index int the tileImages ArrayList
			int index = 0;
			for(int y = 0; y < h; y++) {
				for(int x = 0; x < w; x++) {
					int gid = Integer.parseInt(eTiles.get(index).getAttribute("gid"));
					mapTiles[y][x] = new Tile(new Animation(tileImages.get(gid)), x, y, tw, th, gid);
					index++;
				}
			}
			//Return the successfully loaded map
			Map loadedMap = new Map(mapTiles, w, h, tw, th);
			return loadedMap;
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
