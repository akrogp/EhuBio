// https://gist.github.com/vasturiano/12da9071095fbd4df434e60d52d2d58d

const width = window.innerWidth,
height = window.innerHeight,
maxRadius = (Math.min(width, height) / 2) - 5;
var depth;

const formatNumber = d3.format(',d');

const x = d3.scaleLinear()
	.range([0, 2 * Math.PI])
	.clamp(true);

/*const y = d3.scaleSqrt()
	.range([maxRadius*.1, maxRadius]);*/

const y = d3.scaleLinear()
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

const textFits = d => {
	if( !d.data.children )
		return false;
	
	const CHAR_SPACE = 6;

	const deltaAngle = x(d.x1) - x(d.x0);
	const r = Math.max(0, (y(d.y0) + y(d.y1)) / 2);
	const perimeter = r * deltaAngle;

	return d.data.name.length * CHAR_SPACE < 0.5*perimeter;
};

const svg = d3.select('body').append('svg')
	.style('width', '100vw')
	.style('height', '100vh')
	.attr('viewBox', `${-width / 2} ${-height / 2} ${width} ${height}`)
	.on('click', () => focusOn()); // Reset zoom on canvas click

d3.json('rest/browse/flare.json', (error, root) => {
	if (error) throw error;

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
		.style('fill', d => color((d.children ? d : d.parent).data.name))
		.attr('d', arc);

	newSlice.append('path')
		.attr('class', 'hidden-arc')		
		.attr('id', (_, i) => `hiddenArc${i}`)
		.attr('d', middleArcLine);

	const text = newSlice.append('text')
		.attr('class', 'parent-text')
		.attr('display', d => textFits(d) ? null : 'none');

	// Add white contour
	text.append('textPath')
		.attr('startOffset','50%')
		.attr('xlink:href', (_, i) => `#hiddenArc${i}` )
		.text(d => d.data.name)
		.style('fill', 'none')
		.style('stroke', '#fff')
		.style('stroke-width', 5)
		.style('stroke-linejoin', 'round');

	text.append('textPath')
		.attr('startOffset','50%')
		.attr('xlink:href', (_, i) => `#hiddenArc${i}` )
		.text(d => d.data.name);
	
	const label = newSlice.append('text')
		.attr('class', 'leaf-text')
		.attr('display', d => d.data.children ? 'none' : null)
		.attr("transform", d => labelTransform(d))
		.text(d => d.data.name);
});

function labelTransform(d) {
    const x = (d.x0 + d.x1) / 2 * 360;
    const y = (d.y0 + d.y1) / 2 * maxRadius;
    return `rotate(${x - 90}) translate(${y},0) rotate(${x < 180 ? 0 : 180})`;
  }

function focusOn(d = { x0: 0, x1: 1, y0: 0, y1: 1 }) {
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
		.attrTween('display', d => () => textFits(d) ? null : 'none');

	transition.selectAll('text.leaf-text')
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