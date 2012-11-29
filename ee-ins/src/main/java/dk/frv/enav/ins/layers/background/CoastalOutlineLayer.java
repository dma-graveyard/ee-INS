/*
 * Copyright 2011 Danish Maritime Authority. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *   1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 *   2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY Danish Maritime Authority ``AS IS'' 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies,
 * either expressed or implied, of Danish Maritime Authority.
 * 
 */
package dk.frv.enav.ins.layers.background;

import java.awt.Graphics;

import com.bbn.openmap.layer.shape.ShapeLayer;
import com.bbn.openmap.layer.shape.SpatialIndex;
import com.bbn.openmap.omGraphics.OMGraphicList;

/**
 * Layer for simple coastal outline background
 */
public class CoastalOutlineLayer extends ShapeLayer {
	
	private static final long serialVersionUID = 1L;
	
	public CoastalOutlineLayer() {
		super();
	}
	
	public void forceRedraw() {
		doPrepare();
	}
	
	@Override
	public synchronized OMGraphicList prepare() {
		if (!isVisible()) {
			return null;
		}
		//long start = System.nanoTime();
		OMGraphicList list = super.prepare();
		//System.out.println("Finished CoastalOutlineLayer.prepare() in " + EeINS.elapsed(start) + " ms\n---");
		return list;
	}
	
	@Override
	public void paint(Graphics g) {
		//long start = System.nanoTime();
		super.paint(g);
		//System.out.println("Finished CoastalOutlineLayer.paint() in " + EeINS.elapsed(start) + " ms\n---");
	}
	
}
