import java.util.ArrayList;import javafx.scene.Group;import javafx.scene.shape.Rectangle;import javafx.scene.control.Label;import javafx.scene.paint.Color;public class _0{private _57[][]map;private static int _1=100;private static int _2=4;private static int _3=1;private static Group _4;private _42 absRoot;public _0(Group _5){map=new _57[_1][_1];absRoot=new _42(0,0,_1,_1);absRoot._54(_3++);_4=_5;_9();}public void _6(){for(int _7=0;_7<_1;_7++){for(int _8=0;_8<_1;_8++){if(map[_7][_8]!=null&&map[_7][_8]._63())System.out.print(map[_7][_8]+" ");else System.out.print("__ ");}System.out.println();}}private void _9(){_9(absRoot,_2);}private void _9(_42 _58,int _10){if(_10>0){if(_58._40()>=_58._41()){int _11=_58._40();int _12=(int)(Math.random()*_11/3)+(_11/3);_42 _43=new _42(_58._38(),_58._39(),_12,_58._41());_42 _44=new _42(_58._38()+_12,_58._39(),_11-_12,_58._41());_43._54(_3++);_44._54(_3++);_58._48(_43);_58._51(_44);_23(_58);Rectangle _13=new Rectangle(1,_58._41(),Color.GREEN);_13.relocate(_58._38()+_12,_58._39());_4.getChildren().add(_13);_9(_43,_10-1);_9(_44,_10-1);}else if(_58._40()<_58._41()){int _14=_58._41();int _15=(int)(Math.random()*_14/3)+(_14/3);_42 _43=new _42(_58._38(),_58._39(),_58._40(),_15);_42 _44=new _42(_58._38(),_58._39()+_15,_58._40(),_14-_15);_43._54(_3++);_44._54(_3++);_58._48(_43);_58._51(_44);_24(_58);Rectangle _13=new Rectangle(_58._40(),1,Color.GREEN);_13.relocate(_58._38(),_58._39()+_15);_4.getChildren().add(_13);_9(_43,_10-1);_9(_44,_10-1);}}else _16(_58);}private void _16(_42 _58){int _17=(int)(((Math.random()*.10)+.10)*_58._40()+1);int _18=(int)(((Math.random()*.10)+.10)*_58._41()+1);int _19=_58._40()-2*_17;int _20=_58._41()-2*_18;int _21=_58._38()+_17;int _22=_58._39()+_18;_58._53(new _29(_21,_22,_19,_20));for(int _7=_22;_7<_22+_20;_7++){for(int _8=_21;_8<_21+_19;_8++){map[_7][_8]=new _57(_58);map[_7][_8]._54(_58._56());map[_7][_8]._61(true);}}Rectangle _13=new Rectangle(_19,_20,Color.RED);_13.relocate(_21,_22);_4.getChildren().add(_13);}private void _23(_42 _45){int _21=_45._47()._38()+_45._47()._40()/2;int _22=_45._39()+_45._41()/2;int _19=_45._50()._38()+_45._50()._40()/2-_21;int _20=1;Rectangle _13=new Rectangle(_19,_20,Color.BLUE);_13.relocate(_21,_22);_4.getChildren().add(_13);for(int _12=_21;_12<=_21+_19;_12++){if(map[_22][_12]==null||!map[_22][_12]._63()){map[_22][_12]=new _57(99);map[_22][_12]._61(true);}}}private void _24(_42 _45){int _21=_45._38()+_45._40()/2;int _22=_45._47()._39()+_45._47()._41()/2;int _19=1;int _20=_45._50()._39()+_45._50()._41()/2-_22;Rectangle _13=new Rectangle(_19,_20,Color.BLUE);_13.relocate(_21,_22);_4.getChildren().add(_13);for(int _15=_22;_15<=_22+_20;_15++){if(map[_15][_21]==null||!map[_15][_21]._63()){map[_15][_21]=new _57(99);map[_15][_21]._61(true);}}}}