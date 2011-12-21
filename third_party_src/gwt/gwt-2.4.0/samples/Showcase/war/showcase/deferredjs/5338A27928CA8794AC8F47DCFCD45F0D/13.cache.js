function jz(){}
function lz(){lz=LCc;kz=new Ozc}
function tz(a){throw new WAc(SHc+a+L6c)}
function pz(){this.b=I6c;nz(this,J6c);if(!this.a){throw new WAc(K6c)}}
function mz(d,a){var b=d.a;for(var c in b){b.hasOwnProperty(c)&&a.ae(c)}}
function R3b(a,b,c,d){var e;a.a.Mg(b,c);e=T3b(a.a.i,b,c);Be(e,d,true)}
function nz(c,b){try{typeof $wnd[b]!=H6c&&tz(b);c.a=$wnd[b]}catch(a){tz(b)}}
function rz(){lz();var a;a=CP(kz.Xd(J6c),51);if(!a){a=new pz;kz.Zd(J6c,a)}return a}
function oz(d,a){a=String(a);var b=d.a;var c=b[a];(c==null||!b.hasOwnProperty(a))&&d.Ed(a);return String(c)}
function Npb(){var a,b,c,d,e,f,g,i,j,k,n;f=new xic;g=new M0b(O6c);g.Q.dir=IFc;g.Q.style[P6c]=DHc;uic(f,new M0b(Q6c));uic(f,g);j=new I3b;b=j.j;i=rz();e=(n=new Xzc,mz(i,n),n);a=0;for(d=Dwc(xC(e.a));d.a.ge();){c=CP(Kwc(d),1);k=oz(i,c);y3b(j,0,a,c);R3b(b,0,a,R6c);y3b(j,1,a,k);R3b(b,1,a,S6c);++a}uic(f,new M0b(T6c));uic(f,j);return f}
var N6c="' in ",L6c="' is not a JavaScript object and cannot be used as a Dictionary",Q6c='<b>Cet exemple interagit avec le JavaScript variable suivant:<\/b>',T6c='<br><br>',O6c='<pre>var userInfo = {\n&nbsp;&nbsp;name: "Amelie Crutcher",\n&nbsp;&nbsp;timeZone: "EST",\n&nbsp;&nbsp;userID: "123",\n&nbsp;&nbsp;lastLogOn: "2/2/2006"\n};<\/pre>\n',M6c="Cannot find '",K6c="Cannot find JavaScript object with the name 'userInfo'",U6c='Dictionary',I6c='Dictionary userInfo',S6c='cw-DictionaryExample-data',R6c='cw-DictionaryExample-header',H6c='object',P6c='textAlign',J6c='userInfo';_=pz.prototype=jz.prototype=new Y;_.gC=function qz(){return HS};_.Ed=function sz(a){var b;b=M6c+a+N6c+this;throw new WAc(b)};_.tS=function uz(){return this.b};_.cM={51:1};_.a=null;_.b=null;var kz;_=Rpb.prototype;_.bc=function Vpb(){X9(this.a,Npb())};var HS=Kqc(OUc,U6c);bDc(sj)(13);