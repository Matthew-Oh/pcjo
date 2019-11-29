5/27/19
Path generation is now based on a binary tree, instead of Connection
-Connection is removed
-RoomRoot is rewritten
	-now a binary tree (holds 2 RoomRoots)
	-no longer utilizes exits
-bspMap is rewritten
	-ArrayList rooms replaced with RoomRoot absRoot (great name btw)
	-containsIdentical() is removed
	-divide() is rewritten
		-creates new RoomRoots to be a children of the original root instead of resizing it and creating a new one
	-path methods are removed
	-generatePaths() is rewritten
		-split into generateHoriPath() and generateVertPath()
-visuals for testing changed
	-paths are blue
	-RoomRoot divisions are now shown as green