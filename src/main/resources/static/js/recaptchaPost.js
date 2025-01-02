// reCAPTCHA v3를 사용하므로 렌더링 여부 불필요
const recaptchaSiteKey = '6Lf345EqAAAAAF9PchwuveYHT-wH-zgzU7uvsW6e'; // v3 사이트 키 설정

// 요청하여 리캡챠 필요 여부 확인
fetch('/wishlink/check-rate-limit')
    .then(response => {
        if (response.ok) {
            return response.json().catch(error => {
                // console.error('Failed to parse JSON response:', error);
                throw new Error('Invalid JSON response');
            });
        } else {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
    })
    .then(data => {
        // console.log('Response data:', data); // JSON 데이터 로그

        if (data && data.requiresCaptcha) {
            // console.log('reCAPTCHA required. Executing reCAPTCHA...');

            // reCAPTCHA v3 실행
            grecaptcha.ready(() => {
                grecaptcha.execute(recaptchaSiteKey, { action: 'submit' }).then(token => {
                    // console.log('Captcha solved! Token:', token);
                    onCaptchaSuccess(token);
                }).catch(error => {
                    // console.error('Error executing reCAPTCHA:', error);
                });
            });
        } else {
            // console.log('No reCAPTCHA required.');
        }
    })
    .catch(error => console.error('Rate limit error:', error));

// reCAPTCHA 인증 성공 후 토큰 처리
function onCaptchaSuccess(token) {
    // console.log('Captcha solved! Token:', token);

    // 서버로 토큰 전송하여 검증
    fetch('/wishlink/verify-captcha', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ token: token })
    })
        .then(response => {
            if (response.ok) {
                // console.log('Captcha verification successful.');
                // alert('리캡챠 인증 성공');
            } else {
                // console.log('Captcha verification failed.');
                alert('리캡챠 인증 실패. 다시 시도해주세요.');
            }
        })
        .catch(error => console.error('Recaptcha Error: ', error));
}
