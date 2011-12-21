function c2(){}
function Z1(){}
function wOb(){}
function AOb(){}
function joc(){}
function qoc(a,b){vv(a.a,b)}
function xOb(a,b){this.b=a;this.a=b}
function BOb(a,b){this.b=a;this.a=b}
function E2(a){return _nb(p2,a)}
function b2(){b2=hRc;a2=new c2}
function Goc(){zoc();Eoc.call(this,Bl($doc,'password'),'gwt-PasswordTextBox')}
function nOb(a,b){zfc(b,'\u0645\u062E\u062A\u0627\u0631\u0629: '+a.Gg()+ASc+a.Hg())}
function Eyc(b){try{var c=b.document.selection.createRange();if(c.parentElement()!==b)return 0;return c.text.length}catch(a){return 0}}
function Dyc(b){try{var c=b.document.selection.createRange();if(c.parentElement()!==b)return -1;return -c.move(_$c,-65535)}catch(a){return 0}}
function mOb(a,b){var c,d;c=new Gkc;c.e[wVc]=4;Dkc(c,a);if(b){d=new Cfc('\u0645\u062E\u062A\u0627\u0631\u0629: 0, 0');Ie(a,new xOb(a,d),(ir(),ir(),hr));Ie(a,new BOb(a,d),(oq(),oq(),nq));Dkc(c,d)}return c}
function Gyc(b){try{var c=b.document.selection.createRange();if(c.parentElement()!==b)return 0;var d=c.text.length;var e=0;var f=c.duplicate();f.moveEnd(_$c,-1);var g=f.text.length;while(g==d&&f.parentElement()==b&&c.compareEndPoints('StartToEnd',f)<=0){e+=2;f.moveEnd(_$c,-1);g=f.text.length}return d+e}catch(a){return 0}}
function Fyc(b){try{var c=b.document.selection.createRange();if(c.parentElement()!==b)return -1;var d=c.duplicate();d.moveToElementText(b);d.setEndPoint('EndToStart',c);var e=d.text.length;var f=0;var g=d.duplicate();g.moveEnd(_$c,-1);var i=g.text.length;while(i==e&&g.parentElement()==b){f+=2;g.moveEnd(_$c,-1);i=g.text.length}return e+f}catch(a){return 0}}
var _$c='character',Z$c='\u0642\u0631\u0627\u0621\u0629 \u0641\u0642\u0637';_=c2.prototype=Z1.prototype=new $1;_.je=function d2(a){return E2((y2(),a))?(fA(),eA):(fA(),dA)};_.gC=function e2(){return d8};var a2;_=rOb.prototype;_.ac=function vOb(){var a,b,c,d,e,f;eqb(this.a,(f=new gxc,f.e[wVc]=5,d=new Doc,Ewc(d.Q,BRc,'cwBasicText-textbox'),qoc(d,(b2(),b2(),a2)),b=new Doc,Ewc(b.Q,BRc,'cwBasicText-textbox-disabled'),b.Q[cXc]=Z$c,uv(b.a),b.Q[$$c]=true,dxc(f,new Jfc('<b>\u0645\u0631\u0628\u0639 \u0646\u0635 \u0639\u0627\u062F\u064A:<\/b>')),dxc(f,mOb(d,true)),dxc(f,mOb(b,false)),c=new Goc,Ewc(c.Q,BRc,'cwBasicText-password'),a=new Goc,Ewc(a.Q,BRc,'cwBasicText-password-disabled'),a.Q[cXc]=Z$c,uv(a.a),a.Q[$$c]=true,dxc(f,new Jfc('<br><br><b>\u0645\u0631\u0628\u0639 \u0646\u0635 \u0643\u0644\u0645\u0629 \u0627\u0644\u0633\u0631:<\/b>')),dxc(f,mOb(c,true)),dxc(f,mOb(a,false)),e=new Iuc,Ewc(e.Q,BRc,'cwBasicText-textarea'),e.Q.rows=5,dxc(f,new Jfc('<br><br><b>\u0645\u0646\u0637\u0642\u0629 \u0627\u0644\u0646\u0635:<\/b>')),dxc(f,mOb(e,true)),f))};_=xOb.prototype=wOb.prototype=new Y;_.gC=function yOb(){return Xcb};_.qc=function zOb(a){nOb(this.b,this.a)};_.cM={27:1,44:1};_.a=null;_.b=null;_=BOb.prototype=AOb.prototype=new Y;_.gC=function COb(){return Ycb};_.oc=function DOb(a){nOb(this.b,this.a)};_.cM={22:1,44:1};_.a=null;_.b=null;_=moc.prototype;_.Gg=function uoc(){return Dyc(this.Q)};_.Hg=function voc(){return Eyc(this.Q)};_=Goc.prototype=joc.prototype=new koc;_.gC=function Hoc(){return Dhb};_.cM={40:1,46:1,84:1,91:1,95:1,110:1,112:1};_=Huc.prototype;_.Gg=function Kuc(){return Fyc(this.Q)};_.Hg=function Luc(){return Gyc(this.Q)};var d8=gFc(uWc,'AnyRtlDirectionEstimator'),Xcb=gFc(HWc,'CwBasicText$2'),Ycb=gFc(HWc,'CwBasicText$3'),Dhb=gFc(eWc,'PasswordTextBox');zRc(sj)(39);