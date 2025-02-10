package np.gov.likhupikemun.dpms.auth.service

import np.gov.likhupikemun.dpms.auth.api.dto.AuthResponse
import np.gov.likhupikemun.dpms.auth.api.dto.LoginRequest
import np.gov.likhupikemun.dpms.auth.api.dto.RegisterRequest
import np.gov.likhupikemun.dpms.auth.api.dto.RequestPasswordResetRequest
import np.gov.likhupikemun.dpms.auth.api.dto.ResetPasswordRequest

interface AuthService {
    fun register(request: RegisterRequest): AuthResponse

    fun login(request: LoginRequest): AuthResponse

    fun refreshToken(refreshToken: String): AuthResponse

    fun logout(token: String)

    fun requestPasswordReset(request: RequestPasswordResetRequest)

    fun resetPassword(request: ResetPasswordRequest)
}
