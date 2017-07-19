package model;

import java.awt.Color;
import java.util.HashMap;

public class ColorHash {
	public HashMap<Integer, Color> colorHash;

	//creates hash from (ldraw color id #)->(actual corrosponding color) as defined here: http://www.ldraw.org/Article93.html
	//also: adds in a default 16 (recurse) just in case... default 16 is red
	//its ugly and heavy in repition but its the easiest way to do it
	public ColorHash(){
		HashMap<Integer, Color> colorHash=new HashMap<Integer, Color>();
		//we think:
		//"trans" is transparent legos
		//"pearl" is for pearly shiny legos
		//"sand" is for "rustic look" legos
		//"chrome" is for shiny (like *really* shiny)

		colorHash.put(0,new Color(33,33,33)); //black
		colorHash.put(1,new Color(0,51,178)); //blue
		colorHash.put(2,new Color(0,140,20)); //green
		colorHash.put(3,new Color(0,153,159)); //teal
		colorHash.put(4,new Color(196,0,38)); //red
		colorHash.put(7,new Color(193,194,193)); //gray
		colorHash.put(14,new Color(255,220,0)); //yellow
		colorHash.put(15,new Color(255,255,255)); //white
		colorHash.put(40, new Color(99, 95, 82));//trans gray (smoke)
		colorHash.put(135, new Color(171,173,172)); //pearl gray
		colorHash.put(137, new Color(106,122,150)); //pearl sand blue
		colorHash.put(21, new Color(224,255,176)); //phosphor white (glow in the dark!)
		colorHash.put(13, new Color(249,164,198)); //pink
		colorHash.put(70, new Color(105,64,39)); //reddish brown
		colorHash.put(256, new Color(33,33,33)); //rubber black
		colorHash.put(273, new Color(0,51,178)); //rubber blue
		colorHash.put(375, new Color(193,194,193)); //rubber gray
		colorHash.put(324, new Color(196,0,38)); //rubber red
		colorHash.put(511, new Color(255,255,255)); //rubber white
		colorHash.put(379, new Color(106,122,150)); //sand blue
		colorHash.put(378, new Color(160,188,172)); //sand green
		colorHash.put(335, new Color(191,135,130)); //sand red
		colorHash.put(373, new Color(132,94,132)); //sand violet
		colorHash.put(71, new Color(163,162,164)); //stone gray (new gray)
		colorHash.put(19, new Color(232,207,161)); //tan
		colorHash.put(382, new Color(232,207,161)); //tan
		colorHash.put(33, new Color(0,32,160)); //trans blue (trans dark blue)
		colorHash.put(42, new Color(192,255,0)); //trans flu lime (puke green)
		colorHash.put(57, new Color(249,96,0)); //trans flu orange
		colorHash.put(32, new Color(99,95,82)); //trans gray (smoke)
		colorHash.put(34, new Color(6,100,50)); // trans green
		colorHash.put(41, new Color(174,239,236)); // trans light cyan (trans light blue)
		colorHash.put(45, new Color(223,102,149)); // trans pink
		colorHash.put(36, new Color(196,0,38)); // trans red
		colorHash.put(37, new Color(100,0,97)); //trans violet
		colorHash.put(46, new Color(202,176,0)); //trans yellow
		colorHash.put(11, new Color(51,166,167)); //turquiose (cyan)
		colorHash.put(22, new Color(129,0,123)); //violet (purple)
		colorHash.put(23, new Color(71,50,176)); //violet blue
		colorHash.put(10 , new Color(107, 238, 144)); //Bright Green
		colorHash.put(418 , new Color(107, 238, 144)); //Bright Green Also
		colorHash.put(6, new Color(92, 30, 0)); //Brown
		colorHash.put(334, new Color(225, 110, 19)); //Chrome Gold
		colorHash.put(383, new Color(224, 224, 224)); //Chrome Silver
		colorHash.put(47, new Color(255, 255, 255)); //Clear (trans white) This might need to be altered eventually
		colorHash.put(272, new Color(0, 29, 104)); //Dark Blue
		colorHash.put(8, new Color(99, 95, 82)); //Dark Gray
		colorHash.put(288, new Color(39, 70, 44)); //Dark Green
		colorHash.put(484, new Color(179, 62, 0)); //Dark Orange
		colorHash.put(5, new Color(223, 102, 149)); //Dark Pink
		colorHash.put(320, new Color(120, 0, 28)); //Dark Red, first found in set 10019, Star Wars Rebel Blockade Runner
		colorHash.put(72, new Color(99, 95, 96)); //Dark Stone Gray (New Dark Gray)
		colorHash.put(28, new Color(197, 151, 80)); //Dark Tan
		colorHash.put(366, new Color(209, 131, 4)); //Earth Orange
		colorHash.put(494, new Color(208, 208, 208)); //Electric Contact
		colorHash.put(9, new Color(107, 171, 220)); //Light Blue
		colorHash.put(503, new Color(230, 227, 218)); //Light Gray (from Mosaic sets)
		colorHash.put(17, new Color(186, 255, 206)); //Light Green (Belville, Pastel Green)
		colorHash.put(431, new Color(186, 255, 206)); //Light Green (Belville, Pastel Green) Also
		colorHash.put(462, new Color(254, 133, 122)); //Light Orange (code formerly used for regular orange)
		colorHash.put(12, new Color(255, 133, 122)); //Light Red (Scala, Salmon)
		colorHash.put(463, new Color(255, 133, 122)); //Light Red (Scala, Salmon) Also
		colorHash.put(20, new Color(215, 196, 230)); //Light Violet (Belville)
		colorHash.put(18, new Color(253, 232, 150)); //Light Yellow (Belville)
		colorHash.put(495, new Color(253, 232, 150)); //Light Yellow (Belville) Also
		colorHash.put(27, new Color(215, 240, 0)); //Lime
		colorHash.put(26, new Color(216, 27, 109)); //Magenta
		colorHash.put(25, new Color(216, 27, 109)); //Orange (Formerly 462)
		colorHash.put(134, new Color(147, 135, 103)); //Pearl Copper
		colorHash.put(142, new Color(215, 169, 75)); //Pearl Gold

		//this is the default refrence color(red)
		//can and should change during runtime
		colorHash.put(1,new Color(255, 0, 255));

		this.colorHash=colorHash;
	}

	public HashMap<Integer, Color> getColorHash(){
		return this.colorHash;
	}



}
