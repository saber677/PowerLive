var ws;
var roomid;

function init(roomId, url){
    ws = new WebSocket(url);
    roomid = roomId;
    print(roomId)
    print(url)
    var json = {
        "uid": 0,
        "roomid": roomid,
        "protover": 1,
        "platform": "web",
        "clientver": "1.4.0"
    }
}

function openConnection(){
    ws.onopen = function () {
        console.log("WebSocket 已连接上");
        ws.send(getCertification(JSON.stringify(json)).buffer);
    };
}

function getCertification(json) {
    var bytes = str2bytes(json);  //字符串转bytes
    var n1 = new ArrayBuffer(bytes.length + 16)
    var i = new DataView(n1);
    i.setUint32(0, bytes.length + 16), //封包总大小
        i.setUint16(4, 16), //头部长度
        i.setUint16(6, 1), //协议版本
        i.setUint32(8, 7),  //操作码 7表示认证并加入房间
        i.setUint32(12, 1); //
    for (var r = 0; r < bytes.length; r++){
        i.setUint8(16 + r, bytes[r]); //把要认证的数据添加进去
    }
    return i; //返回
}

function str2bytes(str) {
    var bytes = []
    var c
    var len = str.length
    for (var i = 0; i < len; i++) {
        c = str.charCodeAt(i)
        if (c >= 0x010000 && c <= 0x10FFFF) {
            bytes.push(((c >> 18) & 0x07) | 0xF0)
            bytes.push(((c >> 12) & 0x3F) | 0x80)
            bytes.push(((c >> 6) & 0x3F) | 0x80)
            bytes.push((c & 0x3F) | 0x80)
        } else if (c >= 0x000800 && c <= 0x00FFFF) {
            bytes.push(((c >> 12) & 0x0F) | 0xE0)
            bytes.push(((c >> 6) & 0x3F) | 0x80)
            bytes.push((c & 0x3F) | 0x80)
        } else if (c >= 0x000080 && c <= 0x0007FF) {
            bytes.push(((c >> 6) & 0x1F) | 0xC0)
            bytes.push((c & 0x3F) | 0x80)
        } else {
            bytes.push(c & 0xFF)
        }
    }
    return bytes
}

