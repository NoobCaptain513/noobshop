package com.app.noobshop.security.constant;

/**
 * Method-security expressions shared by protected endpoints.
 */
public final class SecurityExpressions {

    public static final String PRODUCT_MANAGE = "hasAuthority('product:manage')";
    public static final String CATEGORY_MANAGE = "hasAuthority('category:manage')";
    public static final String BANNER_MANAGE = "hasAuthority('banner:manage')";
    public static final String NOTICE_MANAGE = "hasAuthority('notice:manage')";
    public static final String COUPON_RELEASE = "hasAuthority('coupon:release')";
    public static final String RBAC_MANAGE = "hasAuthority('rbac:manage')";
    public static final String FILE_UPLOAD = "hasAuthority('file:upload')";
    public static final String USER_READ = "hasAuthority('user:read')";
    public static final String USER_UPDATE = "hasAuthority('user:update')";
    public static final String CART_READ = "hasAuthority('cart:read')";
    public static final String CART_WRITE = "hasAuthority('cart:write')";
    public static final String ADDRESS_MANAGE = "hasAuthority('address:manage')";
    public static final String COLLECTION_MANAGE = "hasAuthority('collection:manage')";
    public static final String ORDER_READ = "hasAuthority('order:read')";
    public static final String ORDER_CREATE = "hasAuthority('order:create')";
    public static final String ORDER_CANCEL = "hasAuthority('order:cancel')";
    public static final String ORDER_PAY = "hasAuthority('order:pay')";
    public static final String ORDER_RECEIVE = "hasAuthority('order:receive')";
    public static final String COMMENT_WRITE = "hasAuthority('comment:write')";
    public static final String COMMENT_LIKE = "hasAuthority('comment:like')";
    public static final String CHAT_ACCESS = "hasAuthority('chat:access')";
    public static final String FEEDBACK_CREATE = "hasAuthority('feedback:create')";

    private SecurityExpressions() {
    }
}
