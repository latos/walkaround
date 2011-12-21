function Ry(){}
function Ty(){Ty=Ozc;Sy=new Rwc}
function s1b(a,b,c,d){var e;a.a.Eg(b,c);e=u1b(a.a.i,b,c);Ce(e,d,true)}
function Uy(d,a){var b=d.a;for(var c in b){b.hasOwnProperty(c)&&a.$d(c)}}
function Wy(d,a){a=String(a);var b=d.a;var c=b[a];(c==null||!b.hasOwnProperty(a))&&d.Cd(a);return String(c)}
function Zy(){Ty();var a;a=iP(Sy.Vd(MGc),51);if(!a){a=new Xy;Sy.Xd(MGc,a)}return a}
function Vy(c,b){try{typeof $wnd[b]!='object'&&_y(b);c.a=$wnd[b]}catch(a){_y(b)}}
function _y(a){throw new Zxc(JBc+a+"' is not a JavaScript object and cannot be used as a Dictionary")}
function Xy(){this.b='Dictionary userInfo';Vy(this,MGc);if(!this.a){throw new Zxc("Cannot find JavaScript object with the name 'userInfo'")}}
function wob(){var a,b,c,d,e,f,g,i,j,k,n;f=new Pfc;g=new q$b('<pre>var userInfo = {\n&nbsp;&nbsp;name: "Amelie Crutcher",\n&nbsp;&nbsp;timeZone: "EST",\n&nbsp;&nbsp;userID: "123",\n&nbsp;&nbsp;lastLogOn: "2/2/2006"\n};<\/pre>\n');g.Q.dir=bBc;g.Q.style['textAlign']=wBc;Mfc(f,new q$b('<b>Cet exemple interagit avec le JavaScript variable suivant:<\/b>'));Mfc(f,g);j=new j1b;b=j.j;i=Zy();e=(n=new $wc,Uy(i,n),n);a=0;for(d=Gtc(dC(e.a));d.a.ee();){c=iP(Ntc(d),1);k=Wy(i,c);_0b(j,0,a,c);s1b(b,0,a,'cw-DictionaryExample-header');_0b(j,1,a,k);s1b(b,1,a,'cw-DictionaryExample-data');++a}Mfc(f,new q$b('<br><br>'));Mfc(f,j);return f}
var MGc='userInfo';_=Xy.prototype=Ry.prototype=new Y;_.gC=function Yy(){return nS};_.Cd=function $y(a){var b;b="Cannot find '"+a+"' in "+this;throw new Zxc(b)};_.tS=function az(){return this.b};_.cM={51:1};_.a=null;_.b=null;var Sy;_=Aob.prototype;_.ac=function Eob(){U8(this.a,wob())};var nS=Nnc(OEc,'Dictionary');eAc(tj)(13);