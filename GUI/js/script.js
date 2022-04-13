const div = document.getElementById('forced-graph-3d');
const main_panel = document.getElementById('main_panel');
var myData = {
    "nodes": [
        {
          "id": "id1",
          "name": "name1",
          "val": 1
        },
        {
          "id": "id2",
          "name": "name2",
          "val": 10
        }
    ],
    "links": [
        {
            "source": "id1",
            "target": "id2"
        }
    ]
}


var myGraph = ForceGraph3D();

var height = main_panel.clientHeight*(2/3);
var width = main_panel.clientWidth*(2/3);


myGraph.width(width);
myGraph.height(height);

myGraph(div).graphData(myData);