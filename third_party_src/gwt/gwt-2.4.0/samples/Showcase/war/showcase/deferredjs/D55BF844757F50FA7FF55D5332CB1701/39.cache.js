function SA(){}
function NA(){}
function ckb(){}
function gkb(){}
function RVb(){}
function YVb(a,b){vv(a.a,b)}
function dkb(a,b){this.b=a;this.a=b}
function hkb(a,b){this.b=a;this.a=b}
function sB(a){return IV(dB,a)}
function RA(){RA=Pmc;QA=new SA}
function Vjb(a,b){fNb(b,'\u5DF2\u9009\u62E9: '+a.Df()+goc+a.Ef())}
function mWb(){fWb();kWb.call(this,Bl($doc,'password'),'gwt-PasswordTextBox')}
function k4b(b){try{var c=b.document.selection.createRange();if(c.parentElement()!==b)return 0;return c.text.length}catch(a){return 0}}
function j4b(b){try{var c=b.document.selection.createRange();if(c.parentElement()!==b)return -1;return -c.move(iwc,-65535)}catch(a){return 0}}
function Ujb(a,b){var c,d;c=new mSb;c.e[Qqc]=4;jSb(c,a);if(b){d=new iNb('\u5DF2\u9009\u62E9: 0, 0');Ie(a,new dkb(a,d),(ir(),ir(),hr));Ie(a,new hkb(a,d),(oq(),oq(),nq));jSb(c,d)}return c}
function m4b(b){try{var c=b.document.selection.createRange();if(c.parentElement()!==b)return 0;var d=c.text.length;var e=0;var f=c.duplicate();f.moveEnd(iwc,-1);var g=f.text.length;while(g==d&&f.parentElement()==b&&c.compareEndPoints('StartToEnd',f)<=0){e+=2;f.moveEnd(iwc,-1);g=f.text.length}return d+e}catch(a){return 0}}
function l4b(b){try{var c=b.document.selection.createRange();if(c.parentElement()!==b)return -1;var d=c.duplicate();d.moveToElementText(b);d.setEndPoint('EndToStart',c);var e=d.text.length;var f=0;var g=d.duplicate();g.moveEnd(iwc,-1);var i=g.text.length;while(i==e&&g.parentElement()==b){f+=2;g.moveEnd(iwc,-1);i=g.text.length}return e+f}catch(a){return 0}}
var iwc='character',gwc='\u53EA\u8BFB';_=SA.prototype=NA.prototype=new OA;_.fd=function TA(a){return sB((mB(),a))?(dy(),cy):(dy(),by)};_.gC=function UA(){return MF};var QA;_=Zjb.prototype;_.ac=function bkb(){var a,b,c,d,e,f;NX(this.a,(f=new O2b,f.e[Qqc]=5,d=new jWb,k2b(d.Q,hnc,'cwBasicText-textbox'),YVb(d,(RA(),RA(),QA)),b=new jWb,k2b(b.Q,hnc,'cwBasicText-textbox-disabled'),b.Q[xsc]=gwc,uv(b.a),b.Q[hwc]=true,L2b(f,new pNb('<b>\u5E38\u89C4\u6587\u672C\u6846:<\/b>')),L2b(f,Ujb(d,true)),L2b(f,Ujb(b,false)),c=new mWb,k2b(c.Q,hnc,'cwBasicText-password'),a=new mWb,k2b(a.Q,hnc,'cwBasicText-password-disabled'),a.Q[xsc]=gwc,uv(a.a),a.Q[hwc]=true,L2b(f,new pNb('<br><br><b>\u5BC6\u7801\u6587\u672C\u6846:<\/b>')),L2b(f,Ujb(c,true)),L2b(f,Ujb(a,false)),e=new o0b,k2b(e.Q,hnc,'cwBasicText-textarea'),e.Q.rows=5,L2b(f,new pNb('<br><br><b>\u6587\u672C\u533A\u57DF:<\/b>')),L2b(f,Ujb(e,true)),f))};_=dkb.prototype=ckb.prototype=new Y;_.gC=function ekb(){return EK};_.qc=function fkb(a){Vjb(this.b,this.a)};_.cM={27:1,44:1};_.a=null;_.b=null;_=hkb.prototype=gkb.prototype=new Y;_.gC=function ikb(){return FK};_.oc=function jkb(a){Vjb(this.b,this.a)};_.cM={22:1,44:1};_.a=null;_.b=null;_=UVb.prototype;_.Df=function aWb(){return j4b(this.Q)};_.Ef=function bWb(){return k4b(this.Q)};_=mWb.prototype=RVb.prototype=new SVb;_.gC=function nWb(){return kP};_.cM={40:1,46:1,84:1,91:1,95:1,110:1,112:1};_=n0b.prototype;_.Df=function q0b(){return l4b(this.Q)};_.Ef=function r0b(){return m4b(this.Q)};var MF=Oac(Orc,'AnyRtlDirectionEstimator'),EK=Oac(_rc,'CwBasicText$2'),FK=Oac(_rc,'CwBasicText$3'),kP=Oac(yrc,'PasswordTextBox');fnc(sj)(39);