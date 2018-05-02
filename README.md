# Tower Defense - TeamSmart

## Code Conventions
* Create and name a new directory under tower with lower case, use hyphen to connect words
```
assets/tower/pikachu/pikachu-sprites.gif
```
* Avoid absolute or relative path, use following code instead
```
String path = this.getClass().getClassLoader().getResource("fileName.png").toString();
```
* Put constant at beginning of class with upper case, use underscore to connect words
```
private static final double MOVE_SPEED = 20.0;
```
* Leave the JavaDoc comment when you added, changed code or found, fixed issue with author annotation
```
/**
 * Describe this class, method or what you did
 *
 * @author your name
 */
```

## Scrum Development
5.2
* The game is done, current version 1.0 -> all members

4.23
* Calculator upgrade, version 1.1 -> LiKun Li

4.15
* Demo of trade rate program has finished -> LiKun Li

4.10
* Finished demo code for sprint 1 -> Xinyi Shao
* Finished testing and close the Taiga -> Xinyi Shao
* Uploaded elements sprites and BGM-> Bingkun Lei
* Start GUI programming -> Bingkun Lei

4.09
* Change sprites -> Jiayun Yan

3.30
* Find sprites for battle field and tower -> Jiayun Yan
* Start multi-thread programming for battlefield -> Xinyi Shao

3.27
* High-level game framework design -> Xinyi Shao
* Draw UML and Sequence UML -> Likun Li

3.26
* Discuss high-level game design -> All
