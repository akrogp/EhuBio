// https://gist.github.com/vasturiano/12da9071095fbd4df434e60d52d2d58d

function sunBurst(root) {
	const width = window.innerWidth;
	const height = window.innerHeight;
	const maxRadius = (Math.min(width, height) / 2) - 5;
	var depth = 0;
	
	const formatNumber = d3.format(',d');
	
	const x = d3.scaleLinear()
		.range([0, 2 * Math.PI])
		.clamp(true);
	
	const y = d3
		.scaleLinear()
		//.scalePow()
		//.exponent(1.2)
		.range([0, maxRadius]);
	
	const color = d3.scaleOrdinal(d3.schemeCategory20);
	
	const partition = d3.partition();
	
	const arc = d3.arc()
		.startAngle(d => x(d.x0))
		.endAngle(d => x(d.x1))
		.innerRadius(d => Math.max(0, y(d.y0)))
		.outerRadius(d => Math.max(0, y(d.y1)));
	
	const middleArcLine = d => {
		const halfPi = Math.PI/2;
		const angles = [x(d.x0) - halfPi, x(d.x1) - halfPi];
		const r = Math.max(0, (y(d.y0) + y(d.y1)) / 2);
	
		const middleAngle = (angles[1] + angles[0]) / 2;
		const invertDirection = middleAngle > 0 && middleAngle < Math.PI; // On lower quadrants write text ccw
		if (invertDirection) { angles.reverse(); }
	
		const path = d3.path();
		path.arc(0, 0, r, angles[0], angles[1], invertDirection);
		return path.toString();
	};
	
	const calcColor = d => {
		if( d.data.substrates ) {
			//return 'gold';
			//const grade1 = 127+Math.round(128/d.data.substrates);
			//const grade2 = 215-Math.round(128/d.data.substrates);
			//const grade2 = grade1;
			//return `rgb(${grade1},${grade2},0)`;
			//const steps = ['#FFFFE0', '#FFFACD', '#F0E68C', '#FFD700', '#DAA520'];
			const steps = ['rgb(255, 255, 0)', 'rgb(240, 240, 0)', 'rgb(220, 220, 0)', 'rgb(200, 200, 0)', 'rgb(150, 150, 0)'];
			if( d.data.substrates <= 4 )
				return steps[0];
			if( d.data.substrates <= 10 )
				return steps[1];
			if( d.data.substrates <= 24 )
				return steps[2];
			if( d.data.substrates <= 50 )
				return steps[3];
			return steps[4];
		} else
			return color((d.children && !d.children[0].data.db ? d : d.parent).data.name);
	}
	
	const labelTransform = d => {
	    const angle = (x(d.x0) + x(d.x1)) / 2 / Math.PI * 180;
	    const shift = (y(d.y0) + y(d.y1)) / 2;
	    return `rotate(${angle - 90}) translate(${shift},0) rotate(${angle < 180 ? 0 : 180})`;
	}
	
	const pathFits = d => {
		const CHAR_SPACE = 12;
	
		const deltaAngle = x(d.x1) - x(d.x0);
		const r = Math.max(0, (y(d.y0) + y(d.y1)) / 2);
		const perimeter = r * deltaAngle;
	
		return d.data.name.length * CHAR_SPACE < perimeter;
	}
	
	const rotatedFits = d => {
		const CHAR_SPACE = 9;
		
		const deltaAngle = x(d.x1) - x(d.x0);
		const height = Math.max(0, y(d.y0)) * deltaAngle;
		const width = Math.max(0, y(d.y1)-y(d.y0));
		
		return height > CHAR_SPACE && width > d.data.name.length * CHAR_SPACE;
	}
	
	const showPath = d => (d.depth < 3 || d.depth === depth) && pathFits(d);
	
	const showRotated = d => !showPath(d) && d.depth > depth && rotatedFits(d);
	
	const svg = d3.select('body').append('svg')
		.style('width', '100vw')
		.style('height', '100vh')
		.attr('viewBox', `${-width / 2} ${-height / 2} ${width} ${height}`)
		.on('click', () => focusOn()); // Reset zoom on canvas click
	
	/*d3.json(url, (error, root) => {
		if (error) throw error;*/
	
		root = d3.hierarchy(root);
		root.sum(d => d.size);
	
		const slice = svg.selectAll('g.slice')
			.data(partition(root).descendants());
	
		slice.exit().remove();
	
		const newSlice = slice.enter()
			.append('g').attr('class', 'slice')
			.on('click', d => {
				d3.event.stopPropagation();
				focusOn(d);
			});
	
		newSlice.append('title')
			.text(d => d.data.desc ? d.data.desc : d.data.name);
	
		newSlice.append('path')
			.attr('class', 'main-arc')
			.style('fill', calcColor)
			.attr('d', arc);
	
		newSlice.append('path')
			.attr('class', 'hidden-arc')		
			.attr('id', (_, i) => `hiddenArc${i}`)
			.attr('d', middleArcLine);
	
		const text = newSlice.append('text')
			.attr('class', 'parent-text')
			.attr('display', d => showPath(d) ? null : 'none');
	
		// Add white contour
		text.append('textPath')
			.attr('class', 'stroke')
			.attr('startOffset','50%')
			.attr('xlink:href', (_, i) => `#hiddenArc${i}` )
			.text(d => d.data.name);
	
		text.append('textPath')
			.attr('startOffset','50%')
			.attr('xlink:href', (_, i) => `#hiddenArc${i}` )
			.text(d => d.data.name);
		
		/*const labelStroke = newSlice.append('text')
			.attr('class', 'leaf-text stroke')		
			.attr('display', d => showRotated(d) ? null : 'none')
			.attr("transform", labelTransform)
			.text(d => d.data.name);*/
		
		const label = newSlice.append('text')
			.attr('class', 'leaf-text')
			.attr('display', d => showRotated(d) ? null : 'none')
			.attr("transform", labelTransform)
			.text(d => d.data.db ? d.data.desc : d.data.name);
		
	//});
	
	function focusOn(d = { x0: 0, x1: 1, y0: 0, y1: 1, depth: 0 }) {
		if( d.data ) {
			if( (!d.children && d.data.db) || (d.children && d.children[0].data.db) ) {
				window.location.href = `search.xhtml?dub=${d.data.name}`;
				return;
			}
			if( !d.children )
				return;
		}
		
		// Travel back if current node is clicked
		if( depth && d.depth === depth && d.parent )
			d = d.parent;
		depth = d.depth;
	
		const transition = svg.transition()
			.duration(750)
			.tween('scale', () => {
				const xd = d3.interpolate(x.domain(), [d.x0, d.x1]),
				yd = d3.interpolate(y.domain(), [d.y0, 1]);
				return t => { x.domain(xd(t)); y.domain(yd(t)); };
			});
	
		transition.selectAll('path.main-arc')
			.attrTween('d', d => () => arc(d));
	
		transition.selectAll('path.hidden-arc')
			.attrTween('d', d => () => middleArcLine(d));
	
		transition.selectAll('text.parent-text')
			.attrTween('display', d => () => showPath(d) ? null : 'none');
	
		transition.selectAll('text.leaf-text')
			.attrTween('display', d => () => showRotated(d) ? null : 'none')
			.attrTween('transform', d => () => labelTransform(d));
		
		moveStackToFront(d);
	
		//
	
		function moveStackToFront(elD) {
			svg.selectAll('.slice').filter(d => d === elD)
			.each(function(d) {
				this.parentNode.appendChild(this);
				if (d.parent) { moveStackToFront(d.parent); }
			})
		}
	}
}